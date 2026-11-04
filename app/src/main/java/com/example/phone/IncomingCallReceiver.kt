package com.example.phone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            when (stateStr) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    CallStateHolder.updateCallState(
                        incomingNumber = incomingNumber,
                        isRinging = true,
                        isOffHook = false
                    )
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    CallStateHolder.updateCallState(
                        incomingNumber = incomingNumber,
                        isRinging = false,
                        isOffHook = true
                    )
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    CallStateHolder.updateCallState(
                        incomingNumber = null,
                        isRinging = false,
                        isOffHook = false
                    )
                }
            }
        }
    }
}
