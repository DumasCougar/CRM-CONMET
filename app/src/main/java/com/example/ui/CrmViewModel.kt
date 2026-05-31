package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CrmDatabase
import com.example.data.CrmRepository
import com.example.data.Lead
import com.example.data.Promoter
import com.example.data.CrmNotification
import com.example.data.NotificationSetting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CrmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CrmRepository
    
    // UI State
    val allPromoters: StateFlow<List<Promoter>>
    val allLeads: StateFlow<List<Lead>>

    private val _currentUser = MutableStateFlow<Promoter?>(null)
    val currentUser: StateFlow<Promoter?> = _currentUser.asStateFlow()

    private val _currentTab = MutableStateFlow(0) // 0: Leads, 1: Pipeline, 2: Comisiones, 3: Análisis
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterOnlyMyLeads = MutableStateFlow(false)
    val filterOnlyMyLeads: StateFlow<Boolean> = _filterOnlyMyLeads.asStateFlow()

    // Login screen states
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Notification states
    val currentNotifications: StateFlow<List<CrmNotification>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getNotificationsForPromoter(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _notificationSetting = MutableStateFlow<NotificationSetting?>(null)
    val notificationSetting: StateFlow<NotificationSetting?> = _notificationSetting.asStateFlow()

    private val _inAppNotification = MutableStateFlow<CrmNotification?>(null)
    val inAppNotification: StateFlow<CrmNotification?> = _inAppNotification.asStateFlow()

    fun clearInAppNotification() {
        _inAppNotification.value = null
    }

    init {
        val database = CrmDatabase.getDatabase(application)
        repository = CrmRepository(database.crmDao())
        
        allPromoters = repository.allPromoters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        allLeads = combine(
            repository.allLeads,
            _currentUser
        ) { leads, user ->
            if (user == null) {
                emptyList()
            } else if (user.username.equals("raviex@gmail.com", ignoreCase = true)) {
                leads
            } else {
                leads.filter { lead ->
                    lead.assignedPromoterId == user.id || lead.createdByPromoterId == user.id
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Listen for user change to sync or seed notification settings
        viewModelScope.launch {
            _currentUser.collect { user ->
                if (user != null) {
                    val setting = repository.getNotificationSettingForPromoter(user.id)
                    if (setting != null) {
                        _notificationSetting.value = setting
                    } else {
                        val defaultSetting = NotificationSetting(promoterId = user.id)
                        repository.insertOrUpdateNotificationSetting(defaultSetting)
                        _notificationSetting.value = defaultSetting
                    }
                } else {
                    _notificationSetting.value = null
                }
            }
        }

        // Seed initial data if database is empty
        viewModelScope.launch {
            allPromoters.take(2).collect { promoters ->
                if (promoters.isEmpty()) {
                    seedDefaultData()
                }
            }
        }
    }

    private suspend fun seedDefaultData() {
        Log.d("CrmViewModel", "Seeding initial data...")
        val admin = Promoter(
            name = "Raviex Administrador",
            username = "raviex@gmail.com",
            passwordHash = "Kalipso13",
            commissionRate = 0.15,
            isActive = true
        )
        repository.insertPromoter(admin)
    }

    // Tab control
    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    // Search query
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Toggle my leads only filter
    fun setFilterOnlyMyLeads(onlyMyLeads: Boolean) {
        _filterOnlyMyLeads.value = onlyMyLeads
    }

    // Authentication
    fun login(username: String, pass: String): Boolean {
        _loginError.value = null
        if (username.isBlank() || pass.isBlank()) {
            _loginError.value = "Por favor ingrese usuario y contraseña"
            return false
        }
        
        val matched = allPromoters.value.find { 
            it.username.equals(username, ignoreCase = true) && it.passwordHash == pass 
        }

        return if (matched != null) {
            if (!matched.isActive) {
                _loginError.value = "Esta cuenta ha sido dada de baja"
                return false
            }
            _currentUser.value = matched
            _currentTab.value = 0 // Ir a Leads por defecto
            true
        } else {
            _loginError.value = "Usuario o contraseña incorrectos"
            false
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
    }

    // Add and Register Promoter
    fun registerPromoter(name: String, user: String, pass: String, commissionRate: Double, onResult: (Boolean, String) -> Unit) {
        if (name.isBlank() || user.isBlank() || pass.isBlank()) {
            onResult(false, "Todos los campos son requeridos")
            return
        }

        viewModelScope.launch {
            val existing = repository.getPromoterByUsername(user)
            if (existing != null) {
                onResult(false, "El nombre de usuario '$user' ya existe")
                return@launch
            }

            val newPromoter = Promoter(
                name = name,
                username = user.lowercase(),
                passwordHash = pass,
                commissionRate = commissionRate
            )
            val newId = repository.insertPromoter(newPromoter)
            if (newId > 0) {
                // Auto-login the new user
                val insertedUsr = repository.getPromoterById(newId.toInt())
                if (insertedUsr != null) {
                    _currentUser.value = insertedUsr
                }
                onResult(true, "Registrado e iniciado sesión con éxito")
            } else {
                onResult(false, "Error al registrar el usuario en base de datos")
            }
        }
    }

    // Add / Update Lead
    fun saveLead(
        id: Int = 0,
        name: String,
        phone: String,
        email: String,
        company: String,
        notes: String,
        status: String,
        dealValue: Double,
        assignedPromoterId: Int,
        operationsCount: Int = 1,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (id == 0) {
                val lead = Lead(
                    id = 0,
                    name = name,
                    phone = phone,
                    email = email,
                    company = company,
                    notes = notes,
                    status = status,
                    dealValue = dealValue,
                    assignedPromoterId = assignedPromoterId,
                    createdByPromoterId = _currentUser.value?.id ?: 0,
                    updatedAt = System.currentTimeMillis(),
                    operationsCount = operationsCount
                )
                val newId = repository.insertLead(lead)
                
                // Trigger NEW_LEAD assignment notification
                triggerNotification(
                    promoterId = assignedPromoterId,
                    title = "¡Nuevo lead asignado!",
                    message = "Te han asignado el lead '$name' de '$company' por un valor de $dealValue€.",
                    type = "NEW_LEAD"
                )
            } else {
                val existing = repository.getLeadById(id)
                val lead = Lead(
                    id = id,
                    name = name,
                    phone = phone,
                    email = email,
                    company = company,
                    notes = notes,
                    status = status,
                    dealValue = dealValue,
                    assignedPromoterId = assignedPromoterId,
                    createdByPromoterId = existing?.createdByPromoterId ?: (_currentUser.value?.id ?: 0),
                    updatedAt = System.currentTimeMillis(),
                    operationsCount = operationsCount
                )
                repository.updateLead(lead)
                
                if (existing != null) {
                    // Check status change
                    if (existing.status != status) {
                        triggerNotification(
                            promoterId = assignedPromoterId,
                            title = "Cambio de Estado",
                            message = "El estado del lead '$name' ha cambiado de '${existing.status}' a '$status'.",
                            type = "STATUS_CHANGE"
                        )
                    }
                    // Check important pipeline updates: value changes
                    if (existing.dealValue != dealValue) {
                        triggerNotification(
                            promoterId = assignedPromoterId,
                            title = "Actualización del Pipeline",
                            message = "El valor de negociación de '$name' cambió de ${existing.dealValue}€ a $dealValue€.",
                            type = "PIPELINE_UPDATE"
                        )
                    }
                    // If assigned promoter has changed!
                    if (existing.assignedPromoterId != assignedPromoterId) {
                        triggerNotification(
                            promoterId = assignedPromoterId,
                            title = "¡Lead reasignado!",
                            message = "Se te ha reasignado el lead '$name' de '$company'.",
                            type = "NEW_LEAD"
                        )
                    }
                }
            }
            onSuccess()
        }
    }

    // Update Lead Status Directly (Pipeline dragging/button simulation)
    fun updateLeadStatus(leadId: Int, newStatus: String) {
        viewModelScope.launch {
            val existing = repository.getLeadById(leadId)
            if (existing != null) {
                val oldStatus = existing.status
                repository.updateLead(existing.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
                
                if (oldStatus != newStatus) {
                    triggerNotification(
                        promoterId = existing.assignedPromoterId,
                        title = "Cambio de Estado",
                        message = "El lead '${existing.name}' cambió de etapa: '$oldStatus' -> '$newStatus'.",
                        type = "STATUS_CHANGE"
                    )
                }
            }
        }
    }

    // Delete Lead
    fun deleteLead(leadId: Int) {
        viewModelScope.launch {
            repository.deleteLead(leadId)
        }
    }

    // Send WhatsApp (Helper)
    fun sendWhatsAppFollowUp(context: Context, lead: Lead) {
        // Build a beautiful personalized CRM follow-up message in Spanish
        val message = """
            Hola ${lead.name}, te escribe ${currentUser.value?.name ?: "un promotor"} de la empresa. 
            Espero que te encuentres muy bien. 
            Te escribo para darle seguimiento a nuestro proyecto/propuesta con la empresa '${lead.company}'. 
            ¿Cuándo tendrías unos minutos libres esta semana para platicar un poco más al respecto? quedo atento. 
            ¡Muchos saludos!
        """.trimIndent()

        // Clean phone number (keep digits/plus sign)
        val cleanPhone = lead.phone.filter { it.isDigit() || it == '+' }
        
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("CrmViewModel", "No se pudo abrir WhatsApp: ${e.message}")
        }
    }

    // Notifications Management
    private fun triggerNotification(
        promoterId: Int,
        title: String,
        message: String,
        type: String
    ) {
        viewModelScope.launch {
            val setting = repository.getNotificationSettingForPromoter(promoterId) ?: NotificationSetting(promoterId = promoterId)
            
            val shouldDeliver = when (type) {
                "NEW_LEAD" -> setting.notifyNewLead
                "STATUS_CHANGE" -> setting.notifyStatusChange
                "PIPELINE_UPDATE" -> setting.notifyPipelineUpdate
                else -> true
            }
            
            if (shouldDeliver) {
                val crmNotification = CrmNotification(
                    promoterId = promoterId,
                    title = title,
                    message = message,
                    type = type,
                    sentPush = setting.receivePush,
                    sentEmail = setting.receiveEmail,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertNotification(crmNotification)
                
                // Show in-app banner alert if current user is the one being notified
                if (_currentUser.value?.id == promoterId) {
                    _inAppNotification.value = crmNotification
                }
            }
        }
    }

    fun updateNotificationSettings(
        notifyNewLead: Boolean,
        notifyStatusChange: Boolean,
        notifyPipelineUpdate: Boolean,
        receivePush: Boolean,
        receiveEmail: Boolean
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = NotificationSetting(
                promoterId = user.id,
                notifyNewLead = notifyNewLead,
                notifyStatusChange = notifyStatusChange,
                notifyPipelineUpdate = notifyPipelineUpdate,
                receivePush = receivePush,
                receiveEmail = receiveEmail
            )
            repository.insertOrUpdateNotificationSetting(updated)
            _notificationSetting.value = updated
        }
    }

    fun markNotificationAsRead(notificationId: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notificationId)
        }
    }

    fun markAllNotificationsAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
        }
    }

    fun clearNotifications() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.clearNotificationsForPromoter(user.id)
        }
    }

    // Active status activation/deactivation
    fun setPromoterActiveStatus(promoterId: Int, isActive: Boolean) {
        viewModelScope.launch {
            val promoter = repository.getPromoterById(promoterId)
            if (promoter != null) {
                repository.updatePromoter(promoter.copy(isActive = isActive))
                
                // If we deactivated the user and they are currently logged in, force a log out
                if (!isActive && _currentUser.value?.id == promoterId) {
                    logout()
                }
            }
        }
    }

    // Direct registration (force active status by default)
    fun registerPromoterByAdmin(name: String, user: String, pass: String, commissionRate: Double, onResult: (Boolean, String) -> Unit) {
        if (name.isBlank() || user.isBlank() || pass.isBlank()) {
            onResult(false, "Todos los campos son requeridos")
            return
        }

        viewModelScope.launch {
            val existing = repository.getPromoterByUsername(user)
            if (existing != null) {
                onResult(false, "El nombre de usuario '$user' ya existe")
                return@launch
            }

            val newPromoter = Promoter(
                name = name,
                username = user.lowercase(),
                passwordHash = pass,
                commissionRate = commissionRate,
                isActive = true
            )
            val newId = repository.insertPromoter(newPromoter)
            if (newId > 0) {
                onResult(true, "Registrado con éxito")
            } else {
                onResult(false, "Error al registrar en base de datos")
            }
        }
    }

    // Update promoter password directly (Admin power)
    fun updatePromoterPassword(promoterId: Int, newPass: String, onResult: (Boolean, String) -> Unit) {
        if (newPass.isBlank()) {
            onResult(false, "La contraseña no puede estar vacía")
            return
        }
        viewModelScope.launch {
            val promoter = repository.getPromoterById(promoterId)
            if (promoter != null) {
                repository.updatePromoter(promoter.copy(passwordHash = newPass))
                onResult(true, "Contraseña actualizada con éxito")
            } else {
                onResult(false, "No se encontró el promotor")
            }
        }
    }

    // Delete promoter (Admin power)
    fun deletePromoter(promoterId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val promoter = repository.getPromoterById(promoterId)
            if (promoter != null) {
                if (promoter.username.equals("raviex@gmail.com", ignoreCase = true)) {
                    onResult(false, "No se puede eliminar al administrador principal")
                    return@launch
                }
                repository.deletePromoter(promoterId)
                onResult(true, "Promotor eliminado con éxito")
            } else {
                onResult(false, "No se encontró el promotor")
            }
        }
    }
}
