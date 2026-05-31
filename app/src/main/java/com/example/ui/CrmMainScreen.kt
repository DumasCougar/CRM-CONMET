package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Lead
import com.example.data.Promoter
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmMainScreen(
    viewModel: CrmViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    val notifications by viewModel.currentNotifications.collectAsState()
    val inAppNotification by viewModel.inAppNotification.collectAsState()
    val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (currentUser == null) {
            LoginScreen(viewModel = viewModel)
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        val initials = currentUser?.name?.split(" ")?.joinToString("") { it.take(1) }?.uppercase() ?: "RP"
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "HOLA, ${currentUser?.name?.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF49454F),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Panel de Control",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        color = Color(0xFF1D1B20)
                                    )
                                }
                            },
                            actions = {
                                // Notification Bell Button
                                IconButton(
                                    onClick = { showNotificationsDialog = true },
                                    modifier = Modifier.padding(end = 4.dp).testTag("alerts_bell_button")
                                ) {
                                    Box(contentAlignment = Alignment.TopEnd) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notificaciones",
                                            tint = Color(0xFF6750A4),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        if (unreadCount > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(17.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFB3261E)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = unreadCount.toString(),
                                                    color = Color.White,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // User initials avatar circle
                                Box(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD0BCFF))
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF21005D),
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.logout() },
                                    modifier = Modifier.testTag("logout_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Cerrar Sesión",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFFFEF7FF) // Match background
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { viewModel.selectTab(0) },
                                icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
                                label = { Text("Clientes") }
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                icon = { Icon(Icons.Default.ViewKanban, contentDescription = "Pipeline") },
                                label = { Text("Pipeline") }
                            )
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { viewModel.selectTab(2) },
                                icon = { Icon(Icons.Default.Percent, contentDescription = "Comisiones") },
                                label = { Text("Reportes") }
                            )
                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { viewModel.selectTab(3) },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Análisis") },
                                label = { Text("Análisis") }
                            )
                            if (currentUser?.username?.equals("raviex@gmail.com", ignoreCase = true) == true) {
                                NavigationBarItem(
                                    selected = currentTab == 4,
                                    onClick = { viewModel.selectTab(4) },
                                    icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = "Usuarios") },
                                    label = { Text("Usuarios") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            0 -> LeadsTabScreen(viewModel = viewModel)
                            1 -> PipelineTabScreen(viewModel = viewModel)
                            2 -> CommissionsTabScreen(viewModel = viewModel)
                            3 -> AnalyticsTabScreen(viewModel = viewModel)
                            4 -> if (currentUser?.username?.equals("raviex@gmail.com", ignoreCase = true) == true) {
                                UsersManagementTabScreen(viewModel = viewModel)
                            } else {
                                LeadsTabScreen(viewModel = viewModel)
                            }
                        }
                    }
                }

                // Floating In-App Banner Accent Card
                AnimatedVisibility(
                    visible = inAppNotification != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    val currentNotif = inAppNotification
                    if (currentNotif != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF21005D)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.markNotificationAsRead(currentNotif.id)
                                    viewModel.clearInAppNotification()
                                    showNotificationsDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val notifIcon = when (currentNotif.type) {
                                    "NEW_LEAD" -> Icons.Default.Groups
                                    "STATUS_CHANGE" -> Icons.Default.Refresh
                                    "PIPELINE_UPDATE" -> Icons.Default.TrendingUp
                                    else -> Icons.Default.Notifications
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD0BCFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = notifIcon,
                                        contentDescription = "Notif",
                                        tint = Color(0xFF21005D),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentNotif.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = currentNotif.message,
                                        color = Color(0xFFEADDFF),
                                        fontSize = 12.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        viewModel.markNotificationAsRead(currentNotif.id)
                                        viewModel.clearInAppNotification()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cerrar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Alerts Control Dialog Box
                NotificationsDialog(
                    show = showNotificationsDialog,
                    onDismiss = { showNotificationsDialog = false },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: CrmViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsState()
    val promoters by viewModel.allPromoters.collectAsState()

    var showRegisterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aesthetic geometric design element
        Box(
            modifier = Modifier
                .size(80.dp)
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2)),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width
                        ),
                        cornerRadius = CornerRadius(24f, 24f)
                    )
                }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CRM CENTRALIZADO",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Acceso seguro para múltiples promotores",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )

                if (loginError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button")
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick login fill card to help test/evaluate easily
        Text(
            text = "Accesos de Promotores de Prueba",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Haz clic para auto-rellenar y entrar:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val quickUsers = listOf(
                    Triple("Administrador (raviex@gmail.com)", "raviex@gmail.com", "Kalipso13")
                )

                quickUsers.forEach { (displayName, uName, pass) ->
                    AssistChip(
                        onClick = {
                            username = uName
                            password = pass
                            viewModel.login(uName, pass)
                        },
                        label = { Text(displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Rellenar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        if (showRegisterDialog) {
            RegisterPromoterDialog(
                viewModel = viewModel,
                onDismiss = { showRegisterDialog = false }
            )
        }
    }
}

@Composable
fun RegisterPromoterDialog(viewModel: CrmViewModel, onDismiss: () -> Unit) {
    var regName by remember { mutableStateOf("") }
    var regUser by remember { mutableStateOf("") }
    var regPass by remember { mutableStateOf("") }
    var regCommRate by remember { mutableStateOf(10.0) } // Default 10%

    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Promotor", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Crea una cuenta para comenzar a registrar leads y comisiones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = regName,
                    onValueChange = { regName = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = regUser,
                    onValueChange = { regUser = it },
                    label = { Text("Nombre de login (Usuario)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = regPass,
                    onValueChange = { regPass = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tasa de Comisión")
                        Text("${regCommRate.toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = regCommRate.toFloat(),
                        onValueChange = { regCommRate = it.toDouble() },
                        valueRange = 1f..30f,
                        steps = 29
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.registerPromoter(
                        name = regName,
                        user = regUser,
                        pass = regPass,
                        commissionRate = regCommRate / 100.0
                    ) { success, msg ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMsg = msg
                        }
                    }
                }
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ----------------------------------------------------
// CLIENTES / LEADS TAB
// ----------------------------------------------------
@Composable
fun LeadsTabScreen(viewModel: CrmViewModel) {
    val context = LocalContext.current
    val leads by viewModel.allLeads.collectAsState()
    val promoters by viewModel.allPromoters.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val myLeadsOnly by viewModel.filterOnlyMyLeads.collectAsState()

    var showAddLeadDialog by remember { mutableStateOf(false) }
    var editingLead by remember { mutableStateOf<Lead?>(null) }
    var selectedStatusFilter by remember { mutableStateOf("Todos") }

    val formattedCurrency = remember { NumberFormat.getCurrencyInstance(Locale("es", "ES")) }

    // Filter leads based on Tab options
    val filteredLeads = leads.filter { lead ->
        val matchesSearch = lead.name.contains(searchQuery, ignoreCase = true) ||
                lead.company.contains(searchQuery, ignoreCase = true) ||
                lead.email.contains(searchQuery, ignoreCase = true)
        
        val matchesOwner = !myLeadsOnly || (lead.assignedPromoterId == currentUser?.id)
        
        val matchesStatus = selectedStatusFilter == "Todos" || lead.status == selectedStatusFilter

        matchesSearch && matchesOwner && matchesStatus
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search / Filter Layout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Buscar lead por nombre, empresa o email...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = myLeadsOnly,
                                onCheckedChange = { viewModel.setFilterOnlyMyLeads(it) }
                            )
                            Text(
                                "Solo mis leads asignados",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Counter
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text("${filteredLeads.size} leads", modifier = Modifier.padding(4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal filter stage chips
                    val pipelineStages = listOf("Todos", "Contacto", "Contactado", "Propuesta", "Negociación", "Ganado", "Perdido")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(pipelineStages) { stage ->
                            FilterChip(
                                selected = selectedStatusFilter == stage,
                                onClick = { selectedStatusFilter = stage },
                                label = { Text(stage) }
                            )
                        }
                    }
                }
            }

            if (filteredLeads.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No se encontraron leads/clientes",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "Intenta cambiar los filtros o agrega un nuevo lead con el botón +",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredLeads) { lead ->
                        val promoterName = promoters.find { it.id == lead.assignedPromoterId }?.name ?: "No asignado"
                        
                        LeadItemCard(
                            lead = lead,
                            promoterName = promoterName,
                            formattedCurrency = formattedCurrency,
                            onSendWhatsapp = { viewModel.sendWhatsAppFollowUp(context, lead) },
                            onEdit = { editingLead = lead },
                            onDelete = { viewModel.deleteLead(lead.id) }
                        )
                    }
                }
            }
        }

        // FAB to add leads
        FloatingActionButton(
            onClick = { showAddLeadDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_lead_button"),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Lead")
        }

        if (showAddLeadDialog) {
            AddOrEditLeadDialog(
                viewModel = viewModel,
                onDismiss = { showAddLeadDialog = false }
            )
        }

        if (editingLead != null) {
            AddOrEditLeadDialog(
                viewModel = viewModel,
                leadToEdit = editingLead,
                onDismiss = { editingLead = null }
            )
        }
    }
}

@Composable
fun LeadItemCard(
    lead: Lead,
    promoterName: String,
    formattedCurrency: NumberFormat,
    onSendWhatsapp: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lead.company,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = lead.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Big deal value tag
                Text(
                    text = formattedCurrency.format(lead.dealValue),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFF2E7D32) // Elegant sales green
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details and chips row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Etapa chip
                val statusBg = when (lead.status) {
                    "Ganado" -> Color(0xFFE8F5E9)
                    "Perdido" -> Color(0xFFFFEBEE)
                    "Negociación" -> Color(0xFFFFF3E0)
                    "Propuesta" -> Color(0xFFF3E5F5)
                    "Contactado" -> Color(0xFFE3F2FD)
                    else -> Color(0xFFECEFF1)
                }
                val statusTc = when (lead.status) {
                    "Ganado" -> Color(0xFF2E7D32)
                    "Perdido" -> Color(0xFFC62828)
                    "Negociación" -> Color(0xFFEF6C00)
                    "Propuesta" -> Color(0xFF6A1B9A)
                    "Contactado" -> Color(0xFF1565C0)
                    else -> Color(0xFF37474F)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lead.status,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = statusTc
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Operaciones",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${lead.operationsCount} op.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Engineering,
                        contentDescription = "Vendedor",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = promoterName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (lead.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lead.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // WhatsApp action button (Filled with whatsapp color green)
                Button(
                    onClick = onSendWhatsapp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366), // WhatsApp Green
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Seguimiento WA", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditLeadDialog(
    viewModel: CrmViewModel,
    leadToEdit: Lead? = null,
    onDismiss: () -> Unit
) {
    val promoters by viewModel.allPromoters.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var name by remember { mutableStateOf(leadToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(leadToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(leadToEdit?.email ?: "") }
    var company by remember { mutableStateOf(leadToEdit?.company ?: "") }
    var notes by remember { mutableStateOf(leadToEdit?.notes ?: "") }
    var status by remember { mutableStateOf(leadToEdit?.status ?: "Contacto") }
    var dealValueStr by remember { mutableStateOf(leadToEdit?.dealValue?.toString() ?: "") }
    var operationsCountStr by remember { mutableStateOf(leadToEdit?.operationsCount?.toString() ?: "1") }
    var selectedPromoterId by remember { mutableStateOf(leadToEdit?.assignedPromoterId ?: currentUser?.id ?: 1) }

    var hasError by remember { mutableStateOf<String?>(null) }
    var isPromoterDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val statusOpts = listOf("Contacto", "Contactado", "Propuesta", "Negociación", "Ganado", "Perdido")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (leadToEdit == null) "Agregar Nuevo Lead" else "Editar Lead",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Empresa / Cliente Corporativo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Contacto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono (+34...)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dealValueStr,
                    onValueChange = { dealValueStr = it },
                    label = { Text("Valor de Venta Estimado (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = operationsCountStr,
                    onValueChange = { operationsCountStr = it },
                    label = { Text("Número de Operaciones (Nº operaciones)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = isStatusDropdownExpanded,
                    onExpandedChange = { isStatusDropdownExpanded = !isStatusDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Etapa del Pipeline") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false }
                    ) {
                        statusOpts.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    status = opt
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Assigned Promoter Dropdown
                ExposedDropdownMenuBox(
                    expanded = isPromoterDropdownExpanded,
                    onExpandedChange = { isPromoterDropdownExpanded = !isPromoterDropdownExpanded }
                ) {
                    val assignedName = promoters.find { it.id == selectedPromoterId }?.name ?: "Seleccione"
                    OutlinedTextField(
                        value = assignedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Asignar Vendedor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPromoterDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isPromoterDropdownExpanded,
                        onDismissRequest = { isPromoterDropdownExpanded = false }
                    ) {
                        promoters.forEach { prom ->
                            DropdownMenuItem(
                                text = { Text(prom.name) },
                                onClick = {
                                    selectedPromoterId = prom.id
                                    isPromoterDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas / Bitácora del Lead") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (hasError != null) {
                    Text(
                        text = hasError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = dealValueStr.toDoubleOrNull()
                    val count = operationsCountStr.toIntOrNull()
                    if (company.isBlank() || name.isBlank() || phone.isBlank()) {
                        hasError = "Empresa, contacto y teléfono son obligatorios"
                    } else if (value == null || value < 0) {
                        hasError = "Valor de venta no válido"
                    } else if (count == null || count < 1) {
                        hasError = "Número de operaciones debe ser al menos 1"
                    } else {
                        viewModel.saveLead(
                            id = leadToEdit?.id ?: 0,
                            name = name,
                            phone = phone,
                            email = email,
                            company = company,
                            notes = notes,
                            status = status,
                            dealValue = value,
                            assignedPromoterId = selectedPromoterId,
                            operationsCount = count,
                            onSuccess = onDismiss
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ----------------------------------------------------
// PIPELINE TAB SCREEN (VISUAL PIPELINE OF STAGES)
// ----------------------------------------------------
@Composable
fun PipelineTabScreen(viewModel: CrmViewModel) {
    val leads by viewModel.allLeads.collectAsState()
    val promoters by viewModel.allPromoters.collectAsState()
    val pipelineStages = listOf("Contacto", "Contactado", "Propuesta", "Negociación", "Ganado", "Perdido")
    val formattedCurrency = remember { NumberFormat.getCurrencyInstance(Locale("es", "ES")) }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Embudo Visual de Ventas (Pipeline)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Gestiona las negociaciones de forma centralizada y cambia de etapa libremente para recalcular las comisiones de inmediato.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Horizontal scrolling board (like Kanban system)
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pipelineStages.forEach { stage ->
                    val leadsInStage = leads.filter { it.status == stage }
                    val totalSum = leadsInStage.sumOf { it.dealValue }

                    PipelineColumn(
                        stageName = stage,
                        leadsCount = leadsInStage.size,
                        totalValue = totalSum,
                        leads = leadsInStage,
                        promoters = promoters,
                        formattedCurrency = formattedCurrency,
                        onChangeStage = { lId, nextStg ->
                            viewModel.updateLeadStatus(lId, nextStg)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PipelineColumn(
    stageName: String,
    leadsCount: Int,
    totalValue: Double,
    leads: List<Lead>,
    promoters: List<Promoter>,
    formattedCurrency: NumberFormat,
    onChangeStage: (Int, String) -> Unit
) {
    val headerColor = when (stageName) {
        "Contacto" -> Color(0xFF90A4AE)
        "Contactado" -> Color(0xFF64B5F6)
        "Propuesta" -> Color(0xFFBA68C8)
        "Negociación" -> Color(0xFFFFB74D)
        "Ganado" -> Color(0xFF81C784)
        else -> Color(0xFFE57373) // Perdido
    }

    Box(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF3EDF7))
            .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(24.dp))
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Category header
            Card(
                colors = CardDefaults.cardColors(containerColor = headerColor),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stageName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Badge(containerColor = Color.White.copy(alpha = 0.25f), contentColor = Color.White) {
                            Text(leadsCount.toString(), fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedCurrency.format(totalValue),
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Leads in Column
            if (leads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sin leads en esta etapa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(leads) { item ->
                        val salesperson = promoters.find { it.id == item.assignedPromoterId }?.name ?: "S/D"
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    item.company,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    formattedCurrency.format(item.dealValue),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Engineering,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        salesperson,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Move buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val stages = listOf("Contacto", "Contactado", "Propuesta", "Negociación", "Ganado", "Perdido")
                                    val curIndex = stages.indexOf(stageName)

                                    IconButton(
                                        onClick = {
                                            if (curIndex > 0) onChangeStage(item.id, stages[curIndex - 1])
                                        },
                                        enabled = curIndex > 0,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Retroceder etapa",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Text(
                                        "Mover Etapa",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )

                                    IconButton(
                                        onClick = {
                                            if (curIndex < stages.size - 1) onChangeStage(item.id, stages[curIndex + 1])
                                        },
                                        enabled = curIndex < stages.size - 1,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Avanzar etapa",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// COMISIONES TAB (REAL-TIME COMISSION CALCULATOR REPORT)
// ----------------------------------------------------
@Composable
fun CommissionsTabScreen(viewModel: CrmViewModel) {
    val leads by viewModel.allLeads.collectAsState()
    val promoters by viewModel.allPromoters.collectAsState()
    val formattedCurrency = remember { NumberFormat.getCurrencyInstance(Locale("es", "ES")) }

    // Aggregate statistics
    val wonLeads = leads.filter { it.status == "Ganado" }
    val totalWonValue = wonLeads.sumOf { it.dealValue }

    val totalCommissionDistributed = wonLeads.sumOf { lead ->
        val promoterOfLead = promoters.find { it.id == lead.assignedPromoterId }
        val rate = promoterOfLead?.commissionRate ?: 0.10
        lead.dealValue * rate
    }

    Scaffold { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals),
            contentPadding = PaddingValues(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Ventas",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Reporte de Comisiones en Tiempo Real",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Total Ventas Ganadas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    formattedCurrency.format(totalWonValue),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Comisiones Totales",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    formattedCurrency.format(totalCommissionDistributed),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = Color(0xFF2E7D32) // Bright highlight
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Desglose por Vendedor / Promotor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(promoters) { promoter ->
                val sellerWonLeads = wonLeads.filter { it.assignedPromoterId == promoter.id }
                val sellerSalesVolume = sellerWonLeads.sumOf { it.dealValue }
                val commissionEarned = sellerSalesVolume * promoter.commissionRate

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar Circle icon
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = promoter.name.take(2).uppercase(),
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        promoter.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "@${promoter.username} • Tasa: ${(promoter.commissionRate * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Dynamic calculations
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Comisión",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    formattedCurrency.format(commissionEarned),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Volumen Ventas: ${formattedCurrency.format(sellerSalesVolume)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Badge {
                                Text("${sellerWonLeads.size} ventas cerradas")
                            }
                        }

                        // Static payout instruction tip
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        var showPaidSnackbar by remember { mutableStateOf(false) }

                        Button(
                            onClick = { showPaidSnackbar = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.End),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Task, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Abonar Liquidación", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (showPaidSnackbar) {
                            AlertDialog(
                                onDismissRequest = { showPaidSnackbar = false },
                                title = { Text("Comisión Abonada") },
                                text = { Text("Se ha registrado la liquidación de ${formattedCurrency.format(commissionEarned)} para ${promoter.name}. Los acumulados volverán a calcularse basados de forma segura en las transacciones vigentes.") },
                                confirmButton = {
                                    TextButton(onClick = { showPaidSnackbar = false }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// ANÁLISIS TAB (PERFORMANCE LEADERBOARD & CHARTS)
// ----------------------------------------------------
@Composable
fun AnalyticsTabScreen(viewModel: CrmViewModel) {
    val leads by viewModel.allLeads.collectAsState()
    val promoters by viewModel.allPromoters.collectAsState()
    val formattedCurrency = remember { NumberFormat.getCurrencyInstance(Locale("es", "ES")) }

    val wonLeads = leads.filter { it.status == "Ganado" }
    val totalWonValue = wonLeads.sumOf { it.dealValue }
    val totalCommissionDistributed = wonLeads.sumOf { lead ->
        val promoterOfLead = promoters.find { it.id == lead.assignedPromoterId }
        val rate = promoterOfLead?.commissionRate ?: 0.10
        lead.dealValue * rate
    }

    // Build the stats list
    val performanceList = promoters.map { prom ->
        val assigned = leads.filter { it.assignedPromoterId == prom.id }
        val closed = assigned.filter { it.status == "Ganado" }
        val closedVolume = closed.sumOf { it.dealValue }
        
        val conversion = if (assigned.isNotEmpty()) {
            (closed.size.toDouble() / assigned.size.toDouble()) * 100
        } else {
            0.0
        }

        PromoterPerformance(
            name = prom.name,
            totalAssignedCount = assigned.size,
            closedCount = closed.size,
            salesVolume = closedVolume,
            conversionRate = conversion
        )
    }.sortedByDescending { it.salesVolume }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        item {
            // Bento Grid Full Dash Layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: Left block (col-1, row-2) and Right block (col-1, row-1 + row-1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Nuevos Leads (Lavender card)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEADDFF)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Icon wrapper
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Leads",
                                    tint = Color(0xFF21005D),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = leads.size.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = Color(0xFF21005D),
                                    lineHeight = 28.sp
                                )
                                Text(
                                    text = "Total Leads",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF21005D).copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Right side: Column containing two separate single-row cards
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 2: WhatsApp hoy (White with outline)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFB3261E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "WhatsApp hoy",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    val contactCount = leads.count { it.status == "Contactado" }
                                    Text(
                                        text = contactCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = "WhatsApp hoy",
                                        fontSize = 10.sp,
                                        color = Color(0xFF49454F),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Card 3: Rendimiento (Ice Blue card)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD3E3FD)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0B57D0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = "Rendimiento",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    val activeCount = leads.filter { it.status != "Perdido" }.size
                                    val total = leads.size
                                    val pct = if (total > 0) ((activeCount.toDouble() / total) * 100).toInt() else 0
                                    Text(
                                        text = "+$pct%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF0B57D0)
                                    )
                                    Text(
                                        text = "Actividad Activa",
                                        fontSize = 10.sp,
                                        color = Color(0xFF0B57D0),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Row 2: Pipeline de Ventas (col-span-2)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pipeline de Ventas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1D1B20)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFFE8DEF8))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Tiempo Real",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6750A4)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val totalCount = leads.size.coerceAtLeast(1)
                        val prospectCount = leads.count { it.status == "Contacto" || it.status == "Contactado" }
                        val negotiationCount = leads.count { it.status == "Propuesta" || it.status == "Negociación" }
                        val closeCount_p = leads.count { it.status == "Ganado" }

                        val prospectP = (prospectCount.toFloat() / totalCount * 100).toInt()
                        val negotiationP = (negotiationCount.toFloat() / totalCount * 100).toInt()
                        val closeP = (closeCount_p.toFloat() / totalCount * 100).toInt()

                        // Progress rows
                        listOf(
                            Triple("Prospección", prospectP, Color(0xFF6750A4)),
                            Triple("Negociación", negotiationP, Color(0xFF625B71)),
                            Triple("Cierres Exitosos", closeP, Color(0xFF7D5260))
                        ).forEach { (label, pctValue, color) ->
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = "$pctValue%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D1B20)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE6E1E5))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(fraction = (pctValue / 100f).coerceIn(0f, 1f))
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }

                // Row 3: Comisiones (col-1, row-2) and Meta de Ventas (col-1, row-2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Comisiones Dark Purple Card
                    Card(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF21005D)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Pagos",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Comisiones",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = formattedCurrency.format(totalCommissionDistributed),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Acumulado mes",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD0BCFF)
                                )
                            }
                        }
                    }

                    // Meta de Ventas White Card with circle indicator
                    Card(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val wonPercent = if (leads.isNotEmpty()) {
                                (wonLeads.size.toFloat() / leads.size * 100).toInt()
                            } else {
                                0
                            }
                            Box(
                                modifier = Modifier.size(64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Background track
                                    drawArc(
                                        color = Color(0xFFE6E1E5),
                                        startAngle = -90f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx())
                                    )
                                    // Active Arc
                                    drawArc(
                                        color = Color(0xFF6750A4),
                                        startAngle = -90f,
                                        sweepAngle = (wonPercent * 3.6f),
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 8.dp.toPx(),
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    )
                                }
                                Text(
                                    text = "$wonPercent%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Meta de Ventas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color(0xFF1D1B20)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Cuadro de Mando Comparativo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(performanceList.size) { index ->
            val perf = performanceList[index]
            val rankIcon = when (index) {
                0 -> Icons.Default.EmojiEvents
                1 -> Icons.Default.Star
                else -> Icons.Default.TrendingUp
            }
            val rankTint = when (index) {
                0 -> Color(0xFFFFD700) // Gold
                1 -> Color(0xFFC0C0C0) // Silver
                else -> MaterialTheme.colorScheme.outline
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = rankIcon,
                                contentDescription = "Rank",
                                tint = rankTint,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = perf.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }

                        Text(
                            text = formattedCurrency.format(perf.salesVolume),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Conversión", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${perf.conversionRate.toInt()}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        Column {
                            Text("Tratos Cerrados", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${perf.closedCount} de ${perf.totalAssignedCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (index == 0 && perf.salesVolume > 0) {
                                Badge(containerColor = Color(0xFFFFF9C4), contentColor = Color(0xFFF57F17)) {
                                    Text("Líder de Ventas", fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp), fontSize = 10.sp)
                                }
                            } else {
                                Box(modifier = Modifier.size(1.dp)) // empty spacer
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceBarChart(
    performanceList: List<PromoterPerformance>,
    formattedCurrency: NumberFormat
) {
    val barColor = MaterialTheme.colorScheme.primary
    val maxVolume = remember(performanceList) { performanceList.maxOfOrNull { it.salesVolume } ?: 1000.0 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            performanceList.take(4).forEach { perf ->
                val fraction = if (maxVolume > 0) (perf.salesVolume / maxVolume) else 0.0

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = perf.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formattedCurrency.format(perf.salesVolume),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Progress Bar drawn on canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                    ) {
                        // Draw track background
                        drawRoundRect(
                            color = Color.LightGray.copy(alpha = 0.25f),
                            size = size,
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Draw animated or filled progress bar directly
                        val fillWidth = size.width * fraction.toFloat()
                        if (fillWidth > 0f) {
                            drawRoundRect(
                                color = barColor,
                                size = Size(width = fillWidth, height = size.height),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }
                    }
                }
            }
        }
    }
}

data class PromoterPerformance(
    val name: String,
    val totalAssignedCount: Int,
    val closedCount: Int,
    val salesVolume: Double,
    val conversionRate: Double
)

@Composable
fun NotificationsDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    viewModel: CrmViewModel
) {
    if (!show) return

    val notifications by viewModel.currentNotifications.collectAsState()
    val settings by viewModel.notificationSetting.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Historial, 1: Canales/Ajustes

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFEF7FF)),
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6750A4))
            ) {
                Text("Listo", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Avisos y Alertas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1D1B20)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF49454F))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                // Tab Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3EDF7))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 0) Color(0xFFEADDFF) else Color.Transparent)
                            .clickable { activeTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Historial",
                            fontSize = 11.sp,
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeTab == 0) Color(0xFF21005D) else Color(0xFF49454F)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 1) Color(0xFFEADDFF) else Color.Transparent)
                            .clickable { activeTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ajustes de Alertas",
                            fontSize = 11.sp,
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeTab == 1) Color(0xFF21005D) else Color(0xFF49454F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (activeTab == 0) {
                    // TAB 0: HISTORIAL
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial (${notifications.size})",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (notifications.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Marcar leído",
                                    fontSize = 10.sp,
                                    color = Color(0xFF6750A4),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.markAllNotificationsAsRead() }
                                )
                                Text(
                                    text = "Limpiar",
                                    fontSize = 10.sp,
                                    color = Color(0xFFB3261E),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.clearNotifications() }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Sin alertas",
                                    tint = Color(0xFFCAC4D0).copy(alpha = 0.5f),
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No tienes notificaciones pendientes",
                                    fontSize = 12.sp,
                                    color = Color(0xFF49454F),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Configura alertas para recibir avisos.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF79747E),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(notifications) { notif ->
                                val notifColor = if (notif.isRead) Color.White else Color(0xFFF3EDF7)
                                val borderStroke = if (notif.isRead) BorderStroke(1.dp, Color(0xFFCAC4D0)) else BorderStroke(1.5.dp, Color(0xFFD0BCFF))
                                val iconType = when(notif.type) {
                                    "NEW_LEAD" -> Icons.Default.Groups
                                    "STATUS_CHANGE" -> Icons.Default.Sync
                                    "PIPELINE_UPDATE" -> Icons.Default.TrendingUp
                                    else -> Icons.Default.Notifications
                                }
                                val iconBg = when(notif.type) {
                                    "NEW_LEAD" -> Color(0xFFEADDFF)
                                    "STATUS_CHANGE" -> Color(0xFFD3E3FD)
                                    "PIPELINE_UPDATE" -> Color(0xFFFEEFC3)
                                    else -> Color(0xFFE8DEF8)
                                }
                                val tintColor = when(notif.type) {
                                    "NEW_LEAD" -> Color(0xFF21005D)
                                    "STATUS_CHANGE" -> Color(0xFF0B57D0)
                                    "PIPELINE_UPDATE" -> Color(0xFFB06000)
                                    else -> Color(0xFF6750A4)
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = notifColor),
                                    border = borderStroke,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(iconBg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = iconType,
                                                    contentDescription = "Tipo",
                                                    tint = tintColor,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = notif.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1D1B20)
                                                    )
                                                    val timeStr = remember(notif.timestamp) {
                                                        val sdf = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                                                        sdf.format(Date(notif.timestamp))
                                                    }
                                                    Text(
                                                        text = timeStr,
                                                        fontSize = 9.sp,
                                                        color = Color(0xFF49454F)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = notif.message,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF49454F)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (notif.sentPush) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(100.dp))
                                                                .background(Color(0xFFE8DEF8))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "Push ✓",
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF21005D)
                                                            )
                                                        }
                                                    }
                                                    if (notif.sentEmail) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(100.dp))
                                                                .background(Color(0xFFD3E3FD))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "Email ✓",
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF0B57D0)
                                                            )
                                                        }
                                                    }
                                                    if (!notif.isRead) {
                                                        Text(
                                                            text = "Nueva",
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFB3261E),
                                                            modifier = Modifier
                                                                .clickable { viewModel.markNotificationAsRead(notif.id) }
                                                                .padding(horizontal = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 1: CANALES
                    val currentSet = settings ?: com.example.data.NotificationSetting(promoterId = 0)
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tipos de alertas deseadas:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )

                        // 1. Nuevos Leads
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                    Text(
                                        text = "Nuevos Leads Asignados",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = "Avisar si me asignan lideres.",
                                        fontSize = 9.sp,
                                        color = Color(0xFF49454F)
                                    )
                                }
                                Switch(
                                    checked = currentSet.notifyNewLead,
                                    onCheckedChange = { checked ->
                                        viewModel.updateNotificationSettings(
                                            notifyNewLead = checked,
                                            notifyStatusChange = currentSet.notifyStatusChange,
                                            notifyPipelineUpdate = currentSet.notifyPipelineUpdate,
                                            receivePush = currentSet.receivePush,
                                            receiveEmail = currentSet.receiveEmail
                                        )
                                    }
                                )
                            }
                        }

                        // 2. Cambios de estado
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                    Text(
                                        text = "Sincronizaciones de Estado",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = "Avisar en cambios de cotizaciones.",
                                        fontSize = 9.sp,
                                        color = Color(0xFF49454F)
                                    )
                                }
                                Switch(
                                    checked = currentSet.notifyStatusChange,
                                    onCheckedChange = { checked ->
                                        viewModel.updateNotificationSettings(
                                            notifyNewLead = currentSet.notifyNewLead,
                                            notifyStatusChange = checked,
                                            notifyPipelineUpdate = currentSet.notifyPipelineUpdate,
                                            receivePush = currentSet.receivePush,
                                            receiveEmail = currentSet.receiveEmail
                                        )
                                    }
                                )
                            }
                        }

                        // 3. Modificaciones de pipeline
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                    Text(
                                        text = "Valor de Negocios (Pipeline)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = "Alertas sobre valor comercial de leads.",
                                        fontSize = 9.sp,
                                        color = Color(0xFF49454F)
                                    )
                                }
                                Switch(
                                    checked = currentSet.notifyPipelineUpdate,
                                    onCheckedChange = { checked ->
                                        viewModel.updateNotificationSettings(
                                            notifyNewLead = currentSet.notifyNewLead,
                                            notifyStatusChange = currentSet.notifyStatusChange,
                                            notifyPipelineUpdate = checked,
                                            receivePush = currentSet.receivePush,
                                            receiveEmail = currentSet.receiveEmail
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Canales de Envío:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )

                        // 4. Push Switch
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Push",
                                        tint = Color(0xFF6750A4),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Canal Push",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color(0xFF1D1B20)
                                        )
                                        Text(
                                            text = "Notificaciones in-app instantanesas",
                                            fontSize = 8.sp,
                                            color = Color(0xFF49454F)
                                        )
                                    }
                                }
                                Switch(
                                    checked = currentSet.receivePush,
                                    onCheckedChange = { checked ->
                                        viewModel.updateNotificationSettings(
                                            notifyNewLead = currentSet.notifyNewLead,
                                            notifyStatusChange = currentSet.notifyStatusChange,
                                            notifyPipelineUpdate = currentSet.notifyPipelineUpdate,
                                            receivePush = checked,
                                            receiveEmail = currentSet.receiveEmail
                                        )
                                    }
                                )
                            }
                        }

                        // 5. Email Switch
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Email",
                                        tint = Color(0xFF0B57D0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Canal Mail",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color(0xFF1D1B20)
                                        )
                                        Text(
                                            text = "Confirmacion por correo",
                                            fontSize = 8.sp,
                                            color = Color(0xFF49454F)
                                        )
                                    }
                                }
                                Switch(
                                    checked = currentSet.receiveEmail,
                                    onCheckedChange = { checked ->
                                        viewModel.updateNotificationSettings(
                                            notifyNewLead = currentSet.notifyNewLead,
                                            notifyStatusChange = currentSet.notifyStatusChange,
                                            notifyPipelineUpdate = currentSet.notifyPipelineUpdate,
                                            receivePush = currentSet.receivePush,
                                            receiveEmail = checked
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersManagementTabScreen(viewModel: CrmViewModel) {
    val promoters by viewModel.allPromoters.collectAsState()
    var showAddPromoterDialog by remember { mutableStateOf(false) }
    var promoterForPasswordReset by remember { mutableStateOf<Promoter?>(null) }
    var promoterForDeletion by remember { mutableStateOf<Promoter?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Gestión de Usuarios",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Activa, desactiva o da de alta promotores",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showAddPromoterDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Alta")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(promoters) { prom ->
                val isSelf = prom.username.equals("raviex@gmail.com", ignoreCase = true)
                UserCardItem(
                    prom = prom,
                    isSelf = isSelf,
                    viewModel = viewModel,
                    onRenewPasswordClick = { selected ->
                        promoterForPasswordReset = selected
                    },
                    onDeleteClick = { selected ->
                        promoterForDeletion = selected
                    }
                )
            }
        }
    }

    if (showAddPromoterDialog) {
        AdminAddPromoterDialog(
            viewModel = viewModel,
            onDismiss = { showAddPromoterDialog = false }
        )
    }

    if (promoterForPasswordReset != null) {
        AdminResetPasswordDialog(
            promoter = promoterForPasswordReset!!,
            viewModel = viewModel,
            onDismiss = { promoterForPasswordReset = null }
        )
    }

    if (promoterForDeletion != null) {
        AdminDeleteUserDialog(
            promoter = promoterForDeletion!!,
            viewModel = viewModel,
            onDismiss = { promoterForDeletion = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCardItem(
    prom: Promoter,
    isSelf: Boolean,
    viewModel: CrmViewModel,
    onRenewPasswordClick: (Promoter) -> Unit,
    onDeleteClick: (Promoter) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prom.isActive) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, if (prom.isActive) Color(0xFFCAC4D0) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = prom.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (prom.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                        )
                        if (isSelf) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("ADMIN", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                    Text(
                        text = prom.username,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                val badgeColor = if (prom.isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                val badgeBg = if (prom.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (prom.isActive) "ALTA" else "BAJA",
                        color = badgeColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFCAC4D0).copy(alpha = 0.5f))

            // Password Viewer & Renewer Sub-Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Contraseña: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (passwordVisible) prom.passwordHash else "••••••••",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                TextButton(
                    onClick = { onRenewPasswordClick(prom) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Renovar contraseña",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Renovar", fontSize = 12.sp)
                }
            }

            HorizontalDivider(color = Color(0xFFCAC4D0).copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comisión: ${(prom.commissionRate * 100).toInt()}%",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (prom.isActive) "Activo" else "Baja",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = prom.isActive,
                        enabled = !isSelf,
                        onCheckedChange = { checked ->
                            viewModel.setPromoterActiveStatus(prom.id, checked)
                        }
                    )
                    if (!isSelf) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { onDeleteClick(prom) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar usuario",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResetPasswordDialog(
    promoter: Promoter,
    viewModel: CrmViewModel,
    onDismiss: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renovar Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Introduce la nueva contraseña para ${promoter.name} (${promoter.username}):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isBlank()) {
                        errorMsg = "La contraseña no puede estar vacía"
                        return@Button
                    }
                    viewModel.updatePromoterPassword(promoter.id, newPassword) { success, msg ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMsg = msg
                        }
                    }
                }
            ) {
                Text("Renovar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDeleteUserDialog(
    promoter: Promoter,
    viewModel: CrmViewModel,
    onDismiss: () -> Unit
) {
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Usuario", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "¿Estás seguro de que deseas eliminar permanentemente al usuario ${promoter.name} (${promoter.username})?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = {
                    viewModel.deletePromoter(promoter.id) { success, msg ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMsg = msg
                        }
                    }
                }
            ) {
                Text("Eliminar", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddPromoterDialog(viewModel: CrmViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var commRate by remember { mutableStateOf(10.0) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dar de Alta Promotor (Nuevo)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Correo Electrónico (Usuario)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tasa de Comisión")
                        Text("${commRate.toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = commRate.toFloat(),
                        onValueChange = { commRate = it.toDouble() },
                        valueRange = 1f..30f,
                        steps = 29
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.registerPromoterByAdmin(
                        name = name,
                        user = user,
                        pass = pass,
                        commissionRate = commRate / 100.0
                    ) { success, msg ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMsg = msg
                        }
                    }
                }
            ) {
                Text("Dar de Alta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
