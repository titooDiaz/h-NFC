package com.example.nfcapp

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class NfcDispatcherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val action = intent?.action
            Log.d("NFC_DISPATCHER", "Intent recibido: $action")

            if (action == NfcAdapter.ACTION_TAG_DISCOVERED ||
                action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
                action == NfcAdapter.ACTION_TECH_DISCOVERED
            ) {
                val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                val messages = rawMsgs?.map { it as NdefMessage }
                val data = messages?.firstOrNull()?.let { NfcUtils.readFromMessage(it) }

                if (data != null) {
                    Log.d("NFC_DISPATCHER", "Tag detectado: $data")
                    Toast.makeText(this, "📡 NFC detectado: $data", Toast.LENGTH_SHORT).show()

                    // 👉 Mandar a MainActivity
                    val activityIntent = Intent(this, MainActivity::class.java).apply {
                        putExtra("nfc_data", data)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(activityIntent)
                } else {
                    Log.w("NFC_DISPATCHER", "No se encontró data en el tag")
                }
            } else {
                Log.w("NFC_DISPATCHER", "Acción NFC inválida: $action")
            }
        } catch (e: Exception) {
            Log.e("NFC_DISPATCHER", "Error procesando NFC", e)
        }

        // Cerramos enseguida para que no quede abierta
        finish()
    }
}
