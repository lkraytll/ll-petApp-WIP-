package com.example.languagelearning.data.repository

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.ErrorUtils
import com.example.languagelearning.data.remote.dto.TtsRequestDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context,
) {

    private var mediaPlayer: MediaPlayer? = null
    private val audioFileName = "ingush_speech.wav"

    suspend fun synthesizeAndPlay(text: String, speaker: String = "inh_0"): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val dto = TtsRequestDto(text = text, speaker = speaker, ssml = false)
            val response = try {
                val res = api.synthesizeIngushApi(dto)
                if (res.isSuccessful) res else api.synthesizeIngush(dto)
            } catch (_: Exception) {
                api.synthesizeIngush(dto)
            }

            if (response.isSuccessful && response.body() != null) {
                val wavFile = File(context.cacheDir, audioFileName)

                withContext(Dispatchers.Main) {
                    stopAudio()
                }

                if (wavFile.exists()) {
                    wavFile.delete()
                }

                response.body()!!.byteStream().use { input ->
                    FileOutputStream(wavFile).use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    playWavFile(wavFile)
                }
            } else {
                val rawError = response.errorBody()?.string()
                val parsed = ErrorUtils.parseErrorMessage(rawError, "TTS Synthesis failed")
                throw Exception(parsed)
            }
        }
    }

    private fun playWavFile(file: File) {
        try {
            stopAudio()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
        }
    }
}
