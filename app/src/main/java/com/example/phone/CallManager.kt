package com.example.phone

import android.content.Context
import android.media.AudioManager
import android.telephony.TelephonyManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActiveCallInfo(
    val incomingNumber: String? = null,
    val callerName: String? = null,
    val isRinging: Boolean = false,
    val isOffHook: Boolean = false,
    val isSpeakerOn: Boolean = false
)

object CallStateHolder {
    private val _callState = MutableStateFlow(ActiveCallInfo())
    val callState: StateFlow<ActiveCallInfo> = _callState.asStateFlow()

    fun updateCallState(
        incomingNumber: String?,
        isRinging: Boolean,
        isOffHook: Boolean
    ) {
        _callState.value = _callState.value.copy(
            incomingNumber = incomingNumber,
            isRinging = isRinging,
            isOffHook = isOffHook
        )
    }

    fun setSpeaker(enabled: Boolean) {
        _callState.value = _callState.value.copy(isSpeakerOn = enabled)
    }
}

class CallManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun getCallAnnouncement(callerNumber: String?): String {
        return "Sir, aapko call aa raha hai."
    }

    fun getCallerQueryResponse(query: String, currentNumber: String?): String {
        val lower = query.lowercase()
        val num = currentNumber ?: "Unknown"
        return when {
            "kiska" in lower || "who" in lower || "caller" in lower -> {
                "Sir, call incoming from $num."
            }
            "number" in lower || "kya number" in lower -> {
                "Sir, the number is $num."
            }
            else -> {
                "Sir, incoming call from $num is currently ringing."
            }
        }
    }

    fun toggleSpeakerphone(enable: Boolean): Boolean {
        return try {
            audioManager?.isSpeakerphoneOn = enable
            CallStateHolder.setSpeaker(enable)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun silenceRinger() {
        try {
            audioManager?.ringerMode = AudioManager.RINGER_MODE_SILENT
        } catch (_: Exception) {}
    }
}
