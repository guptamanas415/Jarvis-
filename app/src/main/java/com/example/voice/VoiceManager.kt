package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) : TextToSpeech.OnInitListener, RecognitionListener {

    companion object {
        private const val TAG = "VoiceManager"
        private const val UTTERANCE_ID = "JARVIS_TTS_STREAM"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _rmsAmplitude = MutableStateFlow(0f)
    val rmsAmplitude: StateFlow<Float> = _rmsAmplitude.asStateFlow()

    private var currentLanguage: String = "ENGLISH"
    private var currentPitch: Float = 1.0f
    private var currentSpeed: Float = 1.0f

    init {
        initializeTts()
        initializeRecognizer()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    private fun initializeRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@VoiceManager)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            updateTtsLanguage(currentLanguage)
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = VoiceState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _voiceState.value = VoiceState.IDLE
                    _rmsAmplitude.value = 0f
                }

                override fun onError(utteranceId: String?) {
                    _voiceState.value = VoiceState.IDLE
                    _rmsAmplitude.value = 0f
                }
            })
        } else {
            Log.e(TAG, "TTS Initialization failed")
        }
    }

    fun setLanguage(lang: String) {
        currentLanguage = lang
        updateTtsLanguage(lang)
    }

    fun setVoiceProfile(pitch: Float, speed: Float) {
        currentPitch = pitch
        currentSpeed = speed
        textToSpeech?.setPitch(pitch)
        textToSpeech?.setSpeechRate(speed)
    }

    private fun updateTtsLanguage(lang: String) {
        if (!isTtsReady) return
        val locale = when (lang.uppercase()) {
            "HINDI" -> Locale.forLanguageTag("hi-IN")
            "HINGLISH" -> Locale.forLanguageTag("en-IN")
            else -> Locale.UK // British sophisticated accent for JARVIS
        }
        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            textToSpeech?.setLanguage(Locale.US)
        }
        textToSpeech?.setPitch(currentPitch)
        textToSpeech?.setSpeechRate(currentSpeed)
    }

    fun startListening() {
        if (_voiceState.value == VoiceState.SPEAKING) {
            stopSpeaking()
        }

        if (speechRecognizer == null) {
            initializeRecognizer()
        }

        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            val langTag = when (currentLanguage.uppercase()) {
                "HINDI" -> "hi-IN"
                "HINGLISH" -> "en-IN"
                else -> "en-GB"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, langTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        try {
            _voiceState.value = VoiceState.LISTENING
            speechRecognizer?.startListening(speechIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition: ${e.message}")
            _voiceState.value = VoiceState.IDLE
            onError("Microphone unavailable or speech recognition error.")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _voiceState.value = VoiceState.IDLE
        _rmsAmplitude.value = 0f
    }

    fun speak(text: String) {
        if (!isTtsReady) {
            Log.w(TAG, "TTS not ready yet")
            return
        }
        stopListening()
        textToSpeech?.setPitch(currentPitch)
        textToSpeech?.setSpeechRate(currentSpeed)
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID)
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID)
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _voiceState.value = VoiceState.IDLE
        _rmsAmplitude.value = 0f
    }

    fun destroy() {
        stopSpeaking()
        stopListening()
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _voiceState.value = VoiceState.LISTENING
    }

    override fun onBeginningOfSpeech() {
        _voiceState.value = VoiceState.LISTENING
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Normalize RMS dB typically ranging between -2 to 10
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
        _rmsAmplitude.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _voiceState.value = VoiceState.PROCESSING
        _rmsAmplitude.value = 0f
    }

    override fun onError(error: Int) {
        _voiceState.value = VoiceState.IDLE
        _rmsAmplitude.value = 0f
        val message = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please try again."
            SpeechRecognizer.ERROR_NETWORK -> "Network required for neural voice recognition."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
            SpeechRecognizer.ERROR_CLIENT -> "Voice client timeout."
            else -> "Speech recognition paused."
        }
        onError(message)
    }

    override fun onResults(results: Bundle?) {
        _voiceState.value = VoiceState.PROCESSING
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val spokenText = matches[0]
            onResult(spokenText)
        } else {
            _voiceState.value = VoiceState.IDLE
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
