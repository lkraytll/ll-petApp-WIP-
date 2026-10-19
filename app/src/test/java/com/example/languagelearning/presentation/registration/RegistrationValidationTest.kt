package com.example.languagelearning.presentation.registration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RegistrationValidationTest {

    @Test
    fun testLoginEmailValidation() {
        val error = validateLoginInput("bad-email", "pass123")
        assertNotNull(error)
        assertEquals("Enter a valid email address", error)
    }

    @Test
    fun testLoginPasswordValidation() {
        val error = validateLoginInput("test@example.com", "")
        assertNotNull(error)
        assertEquals("Enter your password", error)
    }

    @Test
    fun testValidLoginInput() {
        val error = validateLoginInput("valid@example.com", "pass123")
        assertNull(error)
    }

    @Test
    fun testFirstNameBlankValidation() {
        val error = validatePersonalData("", "Doe", "test@example.com")
        assertNotNull(error)
        assertEquals("Enter your first name", error)
    }

    @Test
    fun testLastNameBlankValidation() {
        val error = validatePersonalData("John", "", "test@example.com")
        assertNotNull(error)
        assertEquals("Enter your last name", error)
    }

    @Test
    fun testInvalidEmailValidation() {
        val error = validatePersonalData("John", "Doe", "invalid-email-format")
        assertNotNull(error)
        assertEquals("Enter a valid email address", error)
    }

    @Test
    fun testValidPersonalData() {
        val error = validatePersonalData("John", "Doe", "john.doe@example.com")
        assertNull(error)
    }

    @Test
    fun testPasswordMismatchValidation() {
        val error = validatePasswords("password123", "differentPassword")
        assertNotNull(error)
        assertEquals("Passwords don't match", error)
    }

    private fun validateLoginInput(email: String, password: String): String? {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) return "Enter a valid email address"
        if (trimmedPassword.isBlank()) return "Enter your password"
        return null
    }

    private fun validatePersonalData(firstName: String, lastName: String, email: String): String? {
        val trimmedFirstName = firstName.trim()
        val trimmedLastName = lastName.trim()
        val trimmedEmail = email.trim()

        if (trimmedFirstName.isBlank()) return "Enter your first name"
        if (trimmedLastName.isBlank()) return "Enter your last name"
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) return "Enter a valid email address"

        return null
    }

    private fun validatePasswords(password: String, confirmPassword: String): String? {
        if (password.isBlank()) return "Enter a password"
        if (password != confirmPassword) return "Passwords don't match"
        return null
    }
}
