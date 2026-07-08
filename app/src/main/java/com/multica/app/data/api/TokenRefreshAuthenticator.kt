package com.multica.app.data.api

import com.multica.app.data.TokenManager
import com.multica.app.data.model.TokenRefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenRefreshAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiProvider: dagger.Lazy<MulticaApi>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.refreshToken ?: return null

        synchronized(this) {
            val newToken = runBlocking {
                try {
                    val refreshResponse = apiProvider.get().refreshToken(
                        TokenRefreshRequest(refreshToken)
                    )
                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body()
                        tokenManager.accessToken = body?.token
                        body?.refreshToken?.let { tokenManager.refreshToken = it }
                        body?.token
                    } else {
                        tokenManager.clear()
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (newToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
            return null
        }
    }
}
