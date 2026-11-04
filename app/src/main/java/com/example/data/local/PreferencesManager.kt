package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jarvis_settings_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(prefs.getString("pref_language", "ENGLISH") ?: "ENGLISH")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _voiceProfile = MutableStateFlow(prefs.getString("pref_voice", "Prime") ?: "Prime")
    val voiceProfile: StateFlow<String> = _voiceProfile.asStateFlow()

    private val _voicePitch = MutableStateFlow(prefs.getFloat("pref_voice_pitch", 1.0f))
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _voiceSpeed = MutableStateFlow(prefs.getFloat("pref_voice_speed", 1.0f))
    val voiceSpeed: StateFlow<Float> = _voiceSpeed.asStateFlow()

    private val _sarcasm = MutableStateFlow(prefs.getInt("pref_sarcasm", 75))
    val sarcasm: StateFlow<Int> = _sarcasm.asStateFlow()

    private val _professionalRigor = MutableStateFlow(prefs.getInt("pref_prof", 90))
    val professionalRigor: StateFlow<Int> = _professionalRigor.asStateFlow()

    private val _helpfulness = MutableStateFlow(prefs.getInt("pref_help", 100))
    val helpfulness: StateFlow<Int> = _helpfulness.asStateFlow()

    private val _conciseness = MutableStateFlow(prefs.getInt("pref_concise", 60))
    val conciseness: StateFlow<Int> = _conciseness.asStateFlow()

    private val _wakeWordEnabled = MutableStateFlow(prefs.getBoolean("pref_wakeword", true))
    val wakeWordEnabled: StateFlow<Boolean> = _wakeWordEnabled.asStateFlow()

    private val _hudTheme = MutableStateFlow(prefs.getString("pref_hud_theme", "neon") ?: "neon")
    val hudTheme: StateFlow<String> = _hudTheme.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString("pref_custom_api_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _historyEnabled = MutableStateFlow(prefs.getBoolean("pref_history_enabled", true))
    val historyEnabled: StateFlow<Boolean> = _historyEnabled.asStateFlow()

    fun setLanguage(lang: String) {
        prefs.edit().putString("pref_language", lang).apply()
        _language.value = lang
    }

    fun setVoiceProfile(profile: String) {
        prefs.edit().putString("pref_voice", profile).apply()
        _voiceProfile.value = profile
        // Adjust pitch/rate presets based on profile
        when (profile) {
            "Mark I" -> {
                setVoicePitch(0.85f)
                setVoiceSpeed(0.95f)
            }
            "Prime" -> {
                setVoicePitch(1.0f)
                setVoiceSpeed(1.05f)
            }
            "Nova" -> {
                setVoicePitch(1.2f)
                setVoiceSpeed(1.1f)
            }
            "Echo" -> {
                setVoicePitch(0.95f)
                setVoiceSpeed(1.0f)
            }
        }
    }

    fun setVoicePitch(pitch: Float) {
        prefs.edit().putFloat("pref_voice_pitch", pitch).apply()
        _voicePitch.value = pitch
    }

    fun setVoiceSpeed(speed: Float) {
        prefs.edit().putFloat("pref_voice_speed", speed).apply()
        _voiceSpeed.value = speed
    }

    fun setSarcasm(value: Int) {
        prefs.edit().putInt("pref_sarcasm", value).apply()
        _sarcasm.value = value
    }

    fun setProfessionalRigor(value: Int) {
        prefs.edit().putInt("pref_prof", value).apply()
        _professionalRigor.value = value
    }

    fun setHelpfulness(value: Int) {
        prefs.edit().putInt("pref_help", value).apply()
        _helpfulness.value = value
    }

    fun setConciseness(value: Int) {
        prefs.edit().putInt("pref_concise", value).apply()
        _conciseness.value = value
    }

    fun setWakeWordEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("pref_wakeword", enabled).apply()
        _wakeWordEnabled.value = enabled
    }

    fun setHudTheme(theme: String) {
        prefs.edit().putString("pref_hud_theme", theme).apply()
        _hudTheme.value = theme
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString("pref_custom_api_key", key).apply()
        _customApiKey.value = key
    }

    fun setHistoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("pref_history_enabled", enabled).apply()
        _historyEnabled.value = enabled
    }
}
