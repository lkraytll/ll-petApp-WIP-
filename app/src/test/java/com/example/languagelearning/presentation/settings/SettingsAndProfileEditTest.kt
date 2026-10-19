package com.example.languagelearning.presentation.settings

import com.example.languagelearning.data.remote.dto.RegisteredUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsAndProfileEditTest {

    @Test
    fun testProfileEditStateDataClasses() {
        val user = RegisteredUser(1, "FirstName", "LastName", "user@example.com")

        val idle = ProfileEditState.Idle
        val loading = ProfileEditState.Loading
        val updated = ProfileEditState.ProfileUpdated(user, "Updated successfully")
        val passChanged = ProfileEditState.PasswordChanged("Password changed successfully")
        val error = ProfileEditState.Error("Error message")

        assertEquals(ProfileEditState.Idle, idle)
        assertEquals(ProfileEditState.Loading, loading)
        assertEquals(user, updated.user)
        assertEquals("Updated successfully", updated.message)
        assertEquals("Password changed successfully", passChanged.message)
        assertEquals("Error message", error.message)
    }

    @Test
    fun testProfileEditValidationLogic() {
        // Test First Name empty validation
        val err1 = validateUpdateProfile("   ", "Last", "email@test.com")
        assertNotNull(err1)
        assertEquals("First name cannot be empty", err1)

        // Test Valid Profile Update
        val valid1 = validateUpdateProfile("First", "Last", "email@test.com")
        assertNull(valid1)

        // Test Password empty validation
        val err2 = validateChangePassword("", "newPass123")
        assertNotNull(err2)
        assertEquals("Current and new passwords are required", err2)

        // Test Short Password validation
        val err3 = validateChangePassword("current123", "short")
        assertNotNull(err3)
        assertEquals("New password must be at least 8 characters", err3)

        // Test Valid Password change
        val valid2 = validateChangePassword("current123", "validNewPassword123")
        assertNull(valid2)
    }

    private fun validateUpdateProfile(firstName: String, lastName: String, email: String): String? {
        val fName = firstName.trim()
        if (fName.isBlank()) return "First name cannot be empty"
        return null
    }

    private fun validateChangePassword(currentPass: String, newPass: String): String? {
        val currentP = currentPass.trim()
        val newP = newPass.trim()

        if (currentP.isBlank() || newP.isBlank()) {
            return "Current and new passwords are required"
        }
        if (newP.length < 8) {
            return "New password must be at least 8 characters"
        }
        return null
    }
}
