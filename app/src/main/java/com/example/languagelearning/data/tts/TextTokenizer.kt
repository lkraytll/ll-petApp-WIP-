package com.example.languagelearning.data.tts

import java.util.Locale

class TextTokenizer {

    private val ingMap = mapOf(
        '_' to 0L, ' ' to 1L, '!' to 2L, ',' to 3L, '-' to 4L, '.' to 5L, '?' to 6L,
        'а' to 7L, 'б' to 8L, 'в' to 9L, 'г' to 10L, 'д' to 11L, 'е' to 12L, 'ж' to 13L, 'з' to 14L,
        'и' to 15L, 'й' to 16L, 'к' to 17L, 'л' to 18L, 'м' to 19L, 'н' to 20L, 'о' to 21L, 'п' to 22L,
        'р' to 23L, 'с' to 24L, 'т' to 25L, 'у' to 26L, 'ф' to 27L, 'х' to 28L, 'ц' to 29L, 'ч' to 30L,
        'ш' to 31L, 'щ' to 32L, 'ъ' to 33L, 'ы' to 34L, 'ь' to 35L, 'э' to 36L, 'ю' to 37L, 'я' to 38L,
        'ё' to 39L, '\u04C0' to 40L, '\u04CF' to 40L, '\u04C1' to 40L, '+' to 41L
    )

    fun tokenize(text: String): LongArray {
        val processed = text.lowercase(Locale.ROOT).trim()
            .replace("\u04CF", "\u04C0")
            .replace("\u04C1", "\u04C0")
            .replace("1", "\u04C0")
            .replace(Regex("[^a-zа-яё\u04C0\u04CF0-9\\s!,.-?+]"), "")

        val tokenList = mutableListOf<Long>()

        for (char in processed) {
            val token = ingMap[char]
            if (token != null) {
                tokenList.add(token)
            }
        }

        if (tokenList.isEmpty()) {
            tokenList.add(1L)
        }

        return tokenList.toLongArray()
    }
}
