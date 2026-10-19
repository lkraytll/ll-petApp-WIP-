package com.example.languagelearning.presentation.settings

import android.content.ContextWrapper
import com.example.languagelearning.data.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSettingsViewModelLogout() {
        var clearTokenCalled = false
        val dummyContext = object : ContextWrapper(null) {}
        val tokenManager = object : TokenManager(dummyContext) {
            override suspend fun clearToken() {
                clearTokenCalled = true
            }
        }

        val vm = SettingsViewModel(tokenManager)
        vm.logout()

        assertTrue(clearTokenCalled)
    }
}
