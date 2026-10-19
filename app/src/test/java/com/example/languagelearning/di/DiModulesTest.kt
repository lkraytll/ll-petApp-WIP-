package com.example.languagelearning.di

import android.content.Context
import android.content.ContextWrapper
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.AuthInterceptor
import org.junit.Assert.assertNotNull
import org.junit.Test

class DiModulesTest {

    @Test
    fun testNetworkModuleProvides() {
        val dummyContext = object : ContextWrapper(null) {
            override fun getApplicationContext(): Context {
                return this
            }
        }
        val tokenManager = TokenManager(dummyContext)
        val interceptor = AuthInterceptor(tokenManager)

        val okHttpClient = NetworkModule.provideOkHttpClient(interceptor)
        assertNotNull(okHttpClient)

        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        assertNotNull(retrofit)

        val apiService = NetworkModule.provideApiService(retrofit)
        assertNotNull(apiService)

        val authApiService = NetworkModule.provideAuthApiService(retrofit)
        assertNotNull(authApiService)
    }
}
