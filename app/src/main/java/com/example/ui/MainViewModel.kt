package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.ai.HomeworkAnalysisResult
import com.example.ai.IntentClassifier
import com.example.ai.JarvisIntent
import com.example.commands.CommandExecutor
import com.example.commands.ExecutionResult
import com.example.data.local.ChatMessageEntity
import com.example.data.local.JarvisDatabase
import com.example.data.local.PreferencesManager
import com.example.data.local.RecentCommandEntity
import com.example.phone.CallManager
import com.example.phone.CallStateHolder
import com.example.voice.VoiceManager
import com.example.voice.VoiceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConfirmationDialogState(
    val isVisible: Boolean = false,
    val prompt: String = "",
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getDatabase(application)
    val preferences = PreferencesManager(application)
    val geminiService = GeminiService(application)
    val commandExecutor = CommandExecutor(application)
    val callManager = CallManager(application)
    val intentClassifier = IntentClassifier()

    // Database Flows
    val chatMessages: StateFlow<List<ChatMessageEntity>> = db.chatMessageDao()
        .getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentCommands: StateFlow<List<RecentCommandEntity>> = db.recentCommandDao()
        .getRecentCommands()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Voice Engine
    private var voiceManager: VoiceManager? = null
    val voiceState: StateFlow<VoiceState> get() = voiceManager?.voiceState ?: MutableStateFlow(VoiceState.IDLE)
    val rmsAmplitude: StateFlow<Float> get() = voiceManager?.rmsAmplitude ?: MutableStateFlow(0f)

    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    // Telemetry & Live Clock
    private val _currentTimeString = MutableStateFlow(getCurrentFormattedTime())
    val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

    // Confirmation Modal for sensitive operations (Calling, Shopping, Permissions)
    private val _confirmationState = MutableStateFlow(ConfirmationDialogState())
    val confirmationState: StateFlow<ConfirmationDialogState> = _confirmationState.asStateFlow()

    // Homework State
    private val _homeworkResult = MutableStateFlow<HomeworkAnalysisResult?>(null)
    val homeworkResult: StateFlow<HomeworkAnalysisResult?> = _homeworkResult.asStateFlow()

    private val _isAnalyzingHomework = MutableStateFlow(false)
    val isAnalyzingHomework: StateFlow<Boolean> = _isAnalyzingHomework.asStateFlow()

    // Toast or snackbar messages
    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    init {
        initializeVoiceEngine()
        startTelemetryClock()
        insertSampleInitialDataIfEmpty()
        monitorIncomingCalls()
    }

    private fun initializeVoiceEngine() {
        voiceManager = VoiceManager(
            context = getApplication(),
            onResult = { spokenText ->
                _isVoiceListening.value = false
                handleUserInput(spokenText)
            },
            onError = { errMsg ->
                _isVoiceListening.value = false
                showNotification(errMsg)
            }
        ).apply {
            setLanguage(preferences.language.value)
            setVoiceProfile(preferences.voicePitch.value, preferences.voiceSpeed.value)
        }
    }

    private fun startTelemetryClock() {
        viewModelScope.launch {
            while (true) {
                _currentTimeString.value = getCurrentFormattedTime()
                delay(1000)
            }
        }
    }

    private fun getCurrentFormattedTime(): String {
        return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
    }

    fun toggleVoiceListening() {
        val current = _isVoiceListening.value
        if (current) {
            voiceManager?.stopListening()
            _isVoiceListening.value = false
        } else {
            _isVoiceListening.value = true
            voiceManager?.startListening()
        }
    }

    fun handleUserInput(input: String, imageBitmap: Bitmap? = null, imageUri: String? = null) {
        val cleanInput = input.trim()
        if (cleanInput.isBlank() && imageBitmap == null) return

        viewModelScope.launch {
            // 1. Log user message into chat if history enabled
            if (preferences.historyEnabled.value) {
                db.chatMessageDao().insertMessage(
                    ChatMessageEntity(
                        sender = "USER",
                        content = cleanInput,
                        imageUri = imageUri
                    )
                )
            }

            // 2. Classify intent
            val intent = intentClassifier.classify(cleanInput)

            // If it's a native device action intent (open app, shopping search, call, etc.)
            if (intent !is JarvisIntent.GeneralChat && intent !is JarvisIntent.AcademicHomework) {
                val execResult = commandExecutor.execute(intent)

                if (execResult.requiresConfirmation) {
                    _confirmationState.value = ConfirmationDialogState(
                        isVisible = true,
                        prompt = execResult.confirmationPrompt ?: "Proceed with action, Sir?",
                        onConfirm = {
                            _confirmationState.value = ConfirmationDialogState(isVisible = false)
                            execResult.pendingAction?.invoke()
                            respondJarvis(execResult.feedbackMessage)
                        },
                        onDismiss = {
                            _confirmationState.value = ConfirmationDialogState(isVisible = false)
                            respondJarvis("Operation aborted at your command, Sir.")
                        }
                    )
                } else {
                    respondJarvis(execResult.feedbackMessage)
                }

                // Log into recent commands
                db.recentCommandDao().insertCommand(
                    RecentCommandEntity(
                        prompt = cleanInput,
                        summary = execResult.feedbackMessage,
                        isSuccess = execResult.success
                    )
                )
                return@launch
            }

            // 3. General conversational / academic question -> Delegate to Gemini
            val history = chatMessages.value.takeLast(6).map { it.sender to it.content }
            val geminiResult = geminiService.generateResponse(
                prompt = cleanInput,
                history = history,
                language = preferences.language.value,
                sarcasm = preferences.sarcasm.value,
                professionalRigor = preferences.professionalRigor.value,
                conciseness = preferences.conciseness.value,
                customKey = preferences.customApiKey.value,
                imageBitmap = imageBitmap
            )

            val reply = geminiResult.getOrDefault("System nominal, Sir.")
            respondJarvis(reply)

            // Log command
            db.recentCommandDao().insertCommand(
                RecentCommandEntity(
                    prompt = cleanInput,
                    summary = reply.take(120),
                    isSuccess = true
                )
            )
        }
    }

    fun analyzeHomeworkProblem(imageBitmap: Bitmap?, problemText: String, subject: String) {
        viewModelScope.launch {
            _isAnalyzingHomework.value = true
            try {
                val result = geminiService.analyzeHomework(
                    imageBitmap = imageBitmap,
                    questionText = problemText,
                    subject = subject,
                    customKey = preferences.customApiKey.value
                )
                _homeworkResult.value = result
                voiceManager?.speak(result.summary)
            } catch (e: Exception) {
                showNotification("Homework analysis timeout: ${e.message}")
            } finally {
                _isAnalyzingHomework.value = false
            }
        }
    }

    private fun respondJarvis(text: String) {
        viewModelScope.launch {
            if (preferences.historyEnabled.value) {
                db.chatMessageDao().insertMessage(
                    ChatMessageEntity(
                        sender = "JARVIS",
                        content = text
                    )
                )
            }
            voiceManager?.speak(text)
        }
    }

    private fun monitorIncomingCalls() {
        viewModelScope.launch {
            CallStateHolder.callState.collect { callInfo ->
                if (callInfo.isRinging && !callInfo.incomingNumber.isNullOrBlank()) {
                    val announcement = callManager.getCallAnnouncement(callInfo.incomingNumber)
                    voiceManager?.speak(announcement)
                }
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            db.chatMessageDao().clearAll()
            showNotification("Conversation logs purged successfully.")
        }
    }

    fun clearRecentCommands() {
        viewModelScope.launch {
            db.recentCommandDao().clearAll()
            showNotification("Recent commands cleared.")
        }
    }

    fun showNotification(msg: String) {
        _notificationMessage.value = msg
        viewModelScope.launch {
            delay(3500)
            if (_notificationMessage.value == msg) {
                _notificationMessage.value = null
            }
        }
    }

    fun updateLanguage(lang: String) {
        preferences.setLanguage(lang)
        voiceManager?.setLanguage(lang)
        showNotification("Language dialect set to $lang")
    }

    fun updateVoiceProfile(profile: String) {
        preferences.setVoiceProfile(profile)
        voiceManager?.setVoiceProfile(preferences.voicePitch.value, preferences.voiceSpeed.value)
        showNotification("Voice core switched to $profile")
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager?.destroy()
    }

    private fun insertSampleInitialDataIfEmpty() {
        viewModelScope.launch {
            if (db.chatMessageDao().getAllMessages().stateIn(viewModelScope).value.isEmpty()) {
                db.chatMessageDao().insertMessage(
                    ChatMessageEntity(
                        sender = "USER",
                        content = "JARVIS, analyze this architectural schematic and identify any structural anomalies in the primary load-bearing matrix."
                    )
                )
                db.chatMessageDao().insertMessage(
                    ChatMessageEntity(
                        sender = "JARVIS",
                        content = "Analysis complete. I have detected a micro-fracture propagation in sector 7-G. Below are the telemetry readouts: Stress concentration at 84.2 MPa, vector 41.8° NW.",
                        hasAnalysis = true,
                        analysisTitle = "Anomaly Detected: Structural Matrix",
                        analysisSeverity = "High",
                        stressMetric = "84.2 MPa",
                        vectorMetric = "41.8° NW"
                    )
                )
            }

            if (db.recentCommandDao().getRecentCommands().stateIn(viewModelScope).value.isEmpty()) {
                db.recentCommandDao().insertCommand(
                    RecentCommandEntity(
                        prompt = "JARVIS, run diagnostics on main reactor shielding.",
                        summary = "Shield integrity at 98.4%. No anomalies detected.",
                        isSuccess = true
                    )
                )
                db.recentCommandDao().insertCommand(
                    RecentCommandEntity(
                        prompt = "Summarize overnight global financial telemetry.",
                        summary = "Markets bullish. Tech sector +2.4%.",
                        isSuccess = true
                    )
                )
            }
        }
    }
}
