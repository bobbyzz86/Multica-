package com.multica.app.data.repository

import com.multica.app.data.TokenManager
import com.multica.app.data.api.MulticaApi
import com.multica.app.data.model.AuthRequest
import com.multica.app.data.model.LoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: MulticaApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(AuthRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    tokenManager.accessToken = it.token
                    it.user?.let { user -> tokenManager.currentUserId = user.id }
                }
                Result.success(body!!)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loginWithToken(token: String, workspaceId: String) {
        tokenManager.accessToken = token
        tokenManager.workspaceId = workspaceId
    }

    fun logout() {
        tokenManager.clear()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn

    fun getServerUrl(): String? = tokenManager.serverUrl

    fun setServerUrl(url: String) {
        tokenManager.serverUrl = url
    }
}
