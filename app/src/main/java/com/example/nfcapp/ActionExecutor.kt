package com.example.nfcapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object ActionExecutor {
    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun toggleFlashlight(context: Context) {
        // TODO: implement flashlight toggle (CameraManager)
        Log.d("NFC", "Toggling flashlight")
    }

    fun sendGoogleHomeCommand(device: String, command: String) {
        // TODO: integrate with Google Home API (requires OAuth + Smart Home actions)
        Log.d("NFC", "Sending command $command to $device")
    }
}
