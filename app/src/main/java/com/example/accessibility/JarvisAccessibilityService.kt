package com.example.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "JarvisAccessService"
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        _isServiceActive.value = true
        Log.i(TAG, "JARVIS Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Only inspect events for explicit user commands when permitted
        if (event == null) return
        val packageName = event.packageName?.toString() ?: ""
        Log.d(TAG, "Accessibility event from package: $packageName")
    }

    override fun onInterrupt() {
        Log.w(TAG, "JARVIS Accessibility Service Interrupted")
        _isServiceActive.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceActive.value = false
    }
}
