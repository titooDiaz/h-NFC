package com.example.nfcapp

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.util.Log
import androidx.core.content.getSystemService

object ActionExecutor {

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Log.d("NFC", "Opening URL: $url")
        } catch (e: Exception) {
            Log.e("NFC", "Error opening URL", e)
        }
    }

    fun toggleFlashlight(context: Context, enable: Boolean) {
        try {
            val cameraManager = context.getSystemService<CameraManager>()
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()

            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, enable)
                Log.d("NFC", "Flashlight: $enable")
            }
        } catch (e: Exception) {
            Log.e("NFC", "Error toggling flashlight", e)
        }
    }
}