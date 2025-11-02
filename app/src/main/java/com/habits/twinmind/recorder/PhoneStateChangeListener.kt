package com.habits.twinmind.recorder

import android.content.Context
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.habits.twinmind.ui.stateholder.RecordingStateHolder

class PhoneStateChangeListener(val context : Context)  {

    var telephonyManager : TelephonyManager? = null



    @RequiresApi(Build.VERSION_CODES.S)
    private val telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    RecordingStateHolder.updateOnCallStatus(true)
                    Log.i("PhoneStateChangeListener", "Pausing Recording (RINGING)")
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    RecordingStateHolder.updateOnCallStatus(true)
                    Log.i("PhoneStateChangeListener", "Pausing Recording (OFFHOOK)")
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    RecordingStateHolder.updateOnCallStatus(false)
                    Log.i("PhoneStateChangeListener", "Resuming Recording (IDLE)")
                }
            }
        }
    }

    init {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    }

    fun registerCallStateListener()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager?.registerTelephonyCallback(context.mainExecutor, telephonyCallback)

        } else {
            // Fallback for older devices
//            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    fun unregisterCallStateListener()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            telephonyManager?.unregisterTelephonyCallback(telephonyCallback)
        }
    }




}