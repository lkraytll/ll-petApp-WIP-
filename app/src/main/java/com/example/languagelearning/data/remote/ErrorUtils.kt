package com.example.languagelearning.data.remote

import com.google.gson.JsonParser

object ErrorUtils {

    fun parseErrorMessage(rawErrorBody: String?, fallbackMessage: String): String {
        if (rawErrorBody.isNullOrBlank()) return fallbackMessage

        return try {
            val element = JsonParser.parseString(rawErrorBody)
            if (element.isJsonObject) {
                val jsonObject = element.asJsonObject
                when {
                    jsonObject.has("message") && !jsonObject.get("message").isJsonNull -> {
                        val msg = jsonObject.get("message").asString
                        if (msg.isNotBlank()) msg else fallbackMessage
                    }
                    jsonObject.has("error") && !jsonObject.get("error").isJsonNull -> {
                        val err = jsonObject.get("error").asString
                        if (err.isNotBlank()) err else fallbackMessage
                    }
                    jsonObject.has("detail") && !jsonObject.get("detail").isJsonNull -> {
                        val det = jsonObject.get("detail").asString
                        if (det.isNotBlank()) det else fallbackMessage
                    }
                    else -> fallbackMessage
                }
            } else {
                fallbackMessage
            }
        } catch (_: Exception) {
            val trimmed = rawErrorBody.trim()
            if (!trimmed.startsWith("{") && !trimmed.startsWith("<")) {
                trimmed
            } else {
                fallbackMessage
            }
        }
    }
}
