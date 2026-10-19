package com.example.languagelearning.data.tts

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.LongBuffer

class SileroOnnxTtsManager(
    private val context: Context,
    private val modelFileName: String = "v3_inh.onnx"
) : TtsManager {

    private val tokenizer = TextTokenizer()
    private val audioPlayer = AudioPlayer()
    private val systemFallbackTts = AndroidSystemTtsManager(context)

    private var ortEnv: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    private var isModelLoaded = false

    init {
        loadModelAsync()
    }

    private fun loadModelAsync() {
        Thread {
            try {
                val modelFile = getOrCopyModelFile()
                if (modelFile != null && modelFile.exists()) {
                    ortEnv = OrtEnvironment.getEnvironment()
                    val options = OrtSession.SessionOptions().apply {
                        setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
                    }
                    ortSession = ortEnv?.createSession(modelFile.absolutePath, options)
                    isModelLoaded = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isModelLoaded = false
            }
        }.start()
    }

    private fun getOrCopyModelFile(): File? {
        val candidateNames = listOf(modelFileName, "silero_inh.onnx", "v4_inh.onnx", "silero_tts.onnx")

        for (name in candidateNames) {
            val fileInInternal = File(context.filesDir, name)
            if (fileInInternal.exists() && fileInInternal.length() > 0) {
                return fileInInternal
            }
            try {
                context.assets.open("models/$name").use { input ->
                    FileOutputStream(fileInInternal).use { output ->
                        input.copyTo(output)
                    }
                }
                return fileInInternal
            } catch (_: Exception) {
                // Try next candidate
            }
        }
        return null
    }

    override suspend fun speak(text: String) = withContext(Dispatchers.IO) {
        if (!isModelLoaded || ortSession == null || ortEnv == null) {
            systemFallbackTts.speak(text)
            return@withContext
        }

        try {
            val env = ortEnv ?: return@withContext
            val session = ortSession ?: return@withContext

            val tokens = tokenizer.tokenize(text)
            val tokenBuffer = LongBuffer.wrap(tokens)
            val shape = longArrayOf(1, tokens.size.toLong())

            val inputTensor = OnnxTensor.createTensor(env, tokenBuffer, shape)
            val speakerTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(longArrayOf(0L)), longArrayOf(1))
            val sampleRateTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(longArrayOf(24000L)), longArrayOf(1))

            val inputs = mapOf(
                "input" to inputTensor,
                "speaker" to speakerTensor,
                "sample_rate" to sampleRateTensor
            )

            val results = session.run(inputs)
            val outputTensor = results[0].value

            val audioSamples = when (outputTensor) {
                is Array<*> -> {
                    if (outputTensor.isNotEmpty() && outputTensor[0] is FloatArray) {
                        outputTensor[0] as FloatArray
                    } else null
                }
                is FloatArray -> outputTensor
                else -> null
            }

            inputTensor.close()
            speakerTensor.close()
            sampleRateTensor.close()
            results.close()

            if (audioSamples != null && audioSamples.isNotEmpty()) {
                audioPlayer.playPcmFloat(audioSamples, sampleRate = 24000)
            } else {
                systemFallbackTts.speak(text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            systemFallbackTts.speak(text)
        }
    }

    override fun stop() {
        audioPlayer.stop()
        systemFallbackTts.stop()
    }

    override fun release() {
        stop()
        try {
            ortSession?.close()
            ortEnv?.close()
        } catch (_: Exception) {
        } finally {
            ortSession = null
            ortEnv = null
            isModelLoaded = false
            systemFallbackTts.release()
        }
    }
}
