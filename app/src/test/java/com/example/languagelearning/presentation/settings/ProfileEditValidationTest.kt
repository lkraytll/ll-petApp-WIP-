package com.example.languagelearning.presentation.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileEditValidationTest {

    @Test
    fun testEmptyFirstNameValidation() {
        val error = validateProfileUpdate("   ", "LastName", "email@example.com")
        assertNotNull(error)
        assertEquals("First name cannot be empty", error)
    }

    @Test
    fun testValidProfileUpdate() {
        val error = validateProfileUpdate("FirstName", "LastName", "email@example.com")
        assertNull(error)
    }

    @Test
    fun testBlankCurrentPasswordValidation() {
        val error = validatePasswordChange("", "newPassword123")
        assertNotNull(error)
        assertEquals("Current and new passwords are required", error)
    }

    @Test
    fun testShortNewPasswordValidation() {
        val error = validatePasswordChange("currentPass123", "short")
        assertNotNull(error)
        assertEquals("New password must be at least 8 characters", error)
    }

    @Test
    fun testValidPasswordChange() {
        val error = validatePasswordChange("currentPass123", "validNewPassword123")
        assertNull(error)
    }

    private fun validateProfileUpdate(firstName: String, lastName: String, email: String): String? {
        val fName = firstName.trim()
        if (fName.isBlank()) return "First name cannot be empty"
        return null
    }

    private fun validatePasswordChange(currentPass: String, newPass: String): String? {
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
