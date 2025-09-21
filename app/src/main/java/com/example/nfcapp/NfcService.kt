package com.example.nfcapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class NfcService : Service() {

    companion object {
        private const val CHANNEL_ID = "NFC_CHANNEL"
        private const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NFC Service")
            .setContentText("Esperando etiquetas NFC...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        // Obligatorio para servicios foreground
        startForeground(NOTIF_ID, notification)
        Log.d("NFC_SERVICE", "Servicio NFC iniciado en foreground")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.getStringExtra("nfc_data")
        Log.d("NFC_SERVICE", "onStartCommand recibido: $data")

        if (data != null) {
            Thread {
                try {
                    handleNfcAction(data)
                    updateNotification("Acción NFC ejecutada", data)
                } catch (e: Exception) {
                    Log.e("NFC_SERVICE", "Error procesando NFC: ${e.message}", e)
                    updateNotification("Error NFC", e.message ?: "Desconocido")
                }
            }.start()
        }

        return START_NOT_STICKY
    }

    private fun handleNfcAction(data: String?) {
        val normalized = data?.trim()?.lowercase() ?: return
        Log.d("NFC_SERVICE", "Ejecutando acción con: $normalized")

        when (normalized) {
            "flash on" -> {
                try {
                    turnFlash(true)
                    Log.d("NFC_SERVICE", "Linterna encendida")
                } catch (e: Exception) {
                    Log.e("NFC_SERVICE", "Error al encender linterna: ${e.message}", e)
                }
            }
            "flash off" -> {
                try {
                    turnFlash(false)
                    Log.d("NFC_SERVICE", "Linterna apagada")
                } catch (e: Exception) {
                    Log.e("NFC_SERVICE", "Error al apagar linterna: ${e.message}", e)
                }
            }
            else -> if (normalized.startsWith("http")) {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(browserIntent)
                    Log.d("NFC_SERVICE", "Abriendo URL: $normalized")
                } catch (e: Exception) {
                    Log.e("NFC_SERVICE", "No se pudo abrir URL: ${e.message}", e)
                }
            } else {
                Log.w("NFC_SERVICE", "Acción desconocida: $normalized")
            }
        }
    }

    private fun turnFlash(enable: Boolean) {
        try {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull()
            cameraId?.let {
                cameraManager.setTorchMode(it, enable)
            }
        } catch (se: SecurityException) {
            Log.e("NFC_SERVICE", "Permiso de cámara faltante: ${se.message}")
        } catch (e: Exception) {
            Log.e("NFC_SERVICE", "Error controlando linterna: ${e.message}", e)
        }
    }

    private fun updateNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIF_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NFC Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
