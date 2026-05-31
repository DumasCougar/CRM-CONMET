package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CrmDao {
    // Promotores
    @Query("SELECT * FROM promoters ORDER BY name ASC")
    fun getAllPromoters(): Flow<List<Promoter>>

    @Query("SELECT * FROM promoters WHERE username = :username LIMIT 1")
    suspend fun getPromoterByUsername(username: String): Promoter?

    @Query("SELECT * FROM promoters WHERE id = :id LIMIT 1")
    suspend fun getPromoterById(id: Int): Promoter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromoter(promoter: Promoter): Long

    @Update
    suspend fun updatePromoter(promoter: Promoter)

    @Query("DELETE FROM promoters WHERE id = :id")
    suspend fun deletePromoter(id: Int)

    // Leads / Clientes
    @Query("SELECT * FROM leads ORDER BY updatedAt DESC")
    fun getAllLeads(): Flow<List<Lead>>

    @Query("SELECT * FROM leads WHERE assignedPromoterId = :promoterId ORDER BY updatedAt DESC")
    fun getLeadsByPromoter(promoterId: Int): Flow<List<Lead>>

    @Query("SELECT * FROM leads WHERE id = :id")
    suspend fun getLeadById(id: Int): Lead?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: Lead): Long

    @Update
    suspend fun updateLead(lead: Lead)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLead(id: Int)

    // Notifications
    @Query("SELECT * FROM notifications WHERE promoterId = :promoterId ORDER BY timestamp DESC")
    fun getNotificationsForPromoter(promoterId: Int): Flow<List<CrmNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: CrmNotification): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Int)

    @Query("UPDATE notifications SET isRead = 1 WHERE promoterId = :promoterId")
    suspend fun markAllNotificationsAsRead(promoterId: Int)

    @Query("DELETE FROM notifications WHERE promoterId = :promoterId")
    suspend fun clearNotificationsForPromoter(promoterId: Int)

    // Notification Settings
    @Query("SELECT * FROM notification_settings WHERE promoterId = :promoterId LIMIT 1")
    suspend fun getNotificationSettingForPromoter(promoterId: Int): NotificationSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNotificationSetting(setting: NotificationSetting)
}
