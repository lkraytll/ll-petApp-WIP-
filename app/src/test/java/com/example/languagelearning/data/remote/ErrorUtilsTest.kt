package com.example.languagelearning.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorUtilsTest {

    @Test
    fun testParseJsonMessage() {
        val raw = "{\"message\": \"Invalid credentials\"}"
        val parsed = ErrorUtils.parseErrorMessage(raw, "Fallback")
        assertEquals("Invalid credentials", parsed)
    }

    @Test
    fun testParseJsonErrorKey() {
        val raw = "{\"error\": \"User already exists\"}"
        val parsed = ErrorUtils.parseErrorMessage(raw, "Fallback")
        assertEquals("User already exists", parsed)
    }

    @Test
    fun testParseFallbackOnNull() {
        val parsed = ErrorUtils.parseErrorMessage(null, "Default error")
        assertEquals("Default error", parsed)
    }
}
