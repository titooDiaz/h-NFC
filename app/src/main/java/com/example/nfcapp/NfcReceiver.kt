package com.example.nfcapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.util.Log

class NfcReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED) {

            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val messages = rawMsgs?.map { it as NdefMessage }?.toTypedArray()
            val data = messages?.firstOrNull()?.let { NfcUtils.readFromMessage(it) }

            if (data != null) {
                Log.d("NFC", "Background read: $data")

                val serviceIntent = Intent(context, NfcService::class.java).apply {
                    putExtra("nfc_data", data)
                }
                context.startForegroundService(serviceIntent) // foreground obligatorio en Android 8+
            }
        }
    }
}
