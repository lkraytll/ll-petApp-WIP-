package com.example.languagelearning.data.tts

interface TtsManager {
    /**
     * Synthesizes and plays the provided Ingush text out loud.
     * @param text Ingush text to speak (e.g. "Маьрша воагӀалда")
     */
    suspend fun speak(text: String)

    /**
     * Immediately stops ongoing speech playback.
     */
    fun stop()

    /**
     * Releases system and model resources.
     */
    fun release()
}
