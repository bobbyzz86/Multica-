package com.multica.app.data.repository

import com.multica.app.data.api.MulticaApi
import com.multica.app.data.model.Notification
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val api: MulticaApi
) {
    suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch notifications: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
