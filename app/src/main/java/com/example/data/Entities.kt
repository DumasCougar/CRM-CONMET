package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promoters")
data class Promoter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val username: String,
    val passwordHash: String,
    val commissionRate: Double = 0.10, // Porcentaje de comisión (ej. 0.10 para 10%)
    val isActive: Boolean = true // alta / baja
)

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val company: String,
    val notes: String,
    val status: String, // Etapas: "Contacto", "Contactado", "Propuesta", "Negociación", "Ganado", "Perdido"
    val dealValue: Double,
    val assignedPromoterId: Int,
    val createdByPromoterId: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val operationsCount: Int = 1
)

@Entity(tableName = "notifications")
data class CrmNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val promoterId: Int,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "NEW_LEAD", "STATUS_CHANGE", "PIPELINE_UPDATE"
    val isRead: Boolean = false,
    val sentPush: Boolean = false,
    val sentEmail: Boolean = false
)

@Entity(tableName = "notification_settings")
data class NotificationSetting(
    @PrimaryKey val promoterId: Int,
    val notifyNewLead: Boolean = true,
    val notifyStatusChange: Boolean = true,
    val notifyPipelineUpdate: Boolean = true,
    val receivePush: Boolean = true,
    val receiveEmail: Boolean = true
)

