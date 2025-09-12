package com.example.nfcapp

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.util.Log

class NfcActionService : IntentService("NfcActionService") {

    override fun onHandleIntent(intent: Intent?) {
        val data = intent?.getStringExtra("nfc_data") ?: return

        Log.d("NFC", "Service ejecutando acción con data: $data")

        when (data.uppercase()) {
            "FLASH_ON" -> toggleFlash(true)
            "FLASH_OFF" -> toggleFlash(false)
            else -> if (data.startsWith("http")) {
                openUrlInBackground(data)
            }
        }
    }

    private fun toggleFlash(enable: Boolean) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.first()
        cameraManager.setTorchMode(cameraId, enable)
    }

    private fun openUrlInBackground(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(browserIntent)
    }
}
