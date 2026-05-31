package com.example.data

import kotlinx.coroutines.flow.Flow

class CrmRepository(private val crmDao: CrmDao) {
    val allPromoters: Flow<List<Promoter>> = crmDao.getAllPromoters()
    val allLeads: Flow<List<Lead>> = crmDao.getAllLeads()

    fun getLeadsForPromoter(promoterId: Int): Flow<List<Lead>> {
        return crmDao.getLeadsByPromoter(promoterId)
    }

    suspend fun getPromoterByUsername(username: String): Promoter? {
        return crmDao.getPromoterByUsername(username)
    }

    suspend fun getPromoterById(id: Int): Promoter? {
        return crmDao.getPromoterById(id)
    }

    suspend fun insertPromoter(promoter: Promoter): Long {
        return crmDao.insertPromoter(promoter)
    }

    suspend fun updatePromoter(promoter: Promoter) {
        crmDao.updatePromoter(promoter)
    }

    suspend fun deletePromoter(id: Int) {
        crmDao.deletePromoter(id)
    }

    suspend fun getLeadById(id: Int): Lead? {
        return crmDao.getLeadById(id)
    }

    suspend fun insertLead(lead: Lead): Long {
        return crmDao.insertLead(lead)
    }

    suspend fun updateLead(lead: Lead) {
        crmDao.updateLead(lead)
    }

    suspend fun deleteLead(id: Int) {
        crmDao.deleteLead(id)
    }

    fun getNotificationsForPromoter(promoterId: Int): Flow<List<CrmNotification>> {
        return crmDao.getNotificationsForPromoter(promoterId)
    }

    suspend fun insertNotification(notification: CrmNotification): Long {
        return crmDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(notificationId: Int) {
        crmDao.markNotificationAsRead(notificationId)
    }

    suspend fun markAllNotificationsAsRead(promoterId: Int) {
        crmDao.markAllNotificationsAsRead(promoterId)
    }

    suspend fun clearNotificationsForPromoter(promoterId: Int) {
        crmDao.clearNotificationsForPromoter(promoterId)
    }

    suspend fun getNotificationSettingForPromoter(promoterId: Int): NotificationSetting? {
        return crmDao.getNotificationSettingForPromoter(promoterId)
    }

    suspend fun insertOrUpdateNotificationSetting(setting: NotificationSetting) {
        crmDao.insertOrUpdateNotificationSetting(setting)
    }
}
