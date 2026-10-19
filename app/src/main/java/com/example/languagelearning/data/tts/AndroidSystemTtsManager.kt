package com.example.languagelearning.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AndroidSystemTtsManager(
    context: Context
) : TtsManager, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale("inh", "RU")
        }
    }

    override suspend fun speak(text: String) = withContext(Dispatchers.Main) {
        if (!isInitialized || tts == null) return@withContext

        tts?.language = Locale("inh", "RU")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "LanguageLearningTTS")
    }

    override fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
    }

    override fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        } finally {
            tts = null
            isInitialized = false
        }
    }
}
