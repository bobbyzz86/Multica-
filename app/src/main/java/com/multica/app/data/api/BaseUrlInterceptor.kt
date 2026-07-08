package com.multica.app.data.api

import com.multica.app.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BaseUrlInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val serverUrl = tokenManager.serverUrl ?: return chain.proceed(original)

        val baseHost = original.url.host
        val newUrl = original.url.newBuilder()
            .host(serverUrl.removePrefix("https://").removePrefix("http://")
                .substringBefore("/").trimEnd('/'))
            .scheme(if (serverUrl.startsWith("https")) "https" else "http")
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}
