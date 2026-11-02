
package com.example.languagelearning.data.remote

import android.util.Log
import com.example.languagelearning.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            tokenManager.tokenFlow.firstOrNull()
        }

        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.header(
                "Authorization",
                "Bearer $token"
            )
        }
        Log.i("TOken", "intercept: Got Token")

        return chain.proceed(requestBuilder.build())
    }
}
