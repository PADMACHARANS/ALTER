package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var toneGenerator: ToneGenerator? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var onWordSpokenListener: ((String) -> Unit)? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing ToneGenerator", e)
        }
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            utteranceId?.let { onWordSpokenListener?.invoke(it) }
                        }

                        override fun onDone(utteranceId: String?) {}

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {}
                    })
                }
            }
        }
    }

    fun speak(text: String, rate: Float = 0.9f, pitch: Float = 1.0f) {
        if (isTtsReady) {
            tts?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
            tts?.setPitch(pitch.coerceIn(0.5f, 2.0f))
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.take(20))
        }
    }

    fun stopSpeaking() {
        if (isTtsReady) {
            tts?.stop()
        }
    }

    fun playToneSuccess() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            vibrate(50)
        } catch (e: Exception) {
            Log.e("SoundManager", "Tone error", e)
        }
    }

    fun playToneClick() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 60)
            vibrate(25)
        } catch (e: Exception) {
            Log.e("SoundManager", "Tone error", e)
        }
    }

    fun playToneLetterSound(letter: Char) {
        // Speak the individual phoneme / letter clearly
        val phonemeHint = when (letter.lowercaseChar()) {
            'b' -> "b"
            'd' -> "d"
            'p' -> "p"
            'q' -> "q"
            'm' -> "m"
            'w' -> "w"
            else -> letter.toString()
        }
        speak(phonemeHint, rate = 0.8f, pitch = 1.05f)
        vibrate(35)
    }

    fun playToneFanfare() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
            vibrate(100)
        } catch (e: Exception) {
            Log.e("SoundManager", "Fanfare error", e)
        }
    }

    fun vibrate(durationMs: Long = 40) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore if vibration permissions are not active on device
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            toneGenerator?.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "Release error", e)
        }
    }
}
