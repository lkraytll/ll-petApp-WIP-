package com.example.languagelearning.data.tts

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextTokenizerTest {

    private val tokenizer = TextTokenizer()

    @Test
    fun testIngushTokenization() {
        val tokens = tokenizer.tokenize("воагӀалда")
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun testIngushTokenizationWithPalochka() {
        val tokensWithPalochka = tokenizer.tokenize("кӀ")
        // "к" -> 17, "Ӏ" -> 40
        val expected = longArrayOf(17L, 40L)
        assertArrayEquals(expected, tokensWithPalochka)
    }

    @Test
    fun testIngushDigitOneNormalizationToPalochka() {
        val tokensWithDigitOne = tokenizer.tokenize("к1")
        // "к" -> 17, "1" normalized to "Ӏ" -> 40
        val expected = longArrayOf(17L, 40L)
        assertArrayEquals(expected, tokensWithDigitOne)
    }

    @Test
    fun testEmptyInputFallback() {
        val tokens = tokenizer.tokenize("!!!")
        assertTrue(tokens.isNotEmpty())
    }
}
