package com.example.obdvoiceassist.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale
import com.example.obdvoiceassist.models.AppLanguage

class VoiceAlertManager(
    context: Context
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false

    private var lastSpokenMessage: String? = null
    private var lastSpokenTime: Long = 0L

    private val cooldownMs = 10_000L

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.ENGLISH
            textToSpeech.setSpeechRate(0.95f)
            textToSpeech.setPitch(1.0f)
            isReady = true
        }
    }

    fun setLanguage(language: AppLanguage) {
        if (!isReady) return

        val locale = when (language) {
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.GERMAN -> Locale.GERMAN
        }

        textToSpeech.language = locale
    }
    fun speakWarning(message: String) {
        if (!isReady) return
        if (message.isBlank()) return
        if (message == "No warning detected.") return

        val currentTime = System.currentTimeMillis()

        val sameMessageTooSoon =
            message == lastSpokenMessage && currentTime - lastSpokenTime < cooldownMs

        if (sameMessageTooSoon) return

        lastSpokenMessage = message
        lastSpokenTime = currentTime

        textToSpeech.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "obd_warning"
        )
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}