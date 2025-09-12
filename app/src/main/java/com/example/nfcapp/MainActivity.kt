package com.example.nfcapp

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import android.nfc.tech.Ndef
import android.util.Log

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var mode: String? = null // "WRITE" or "READ"
    private var dataToWrite: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        findViewById<Button>(R.id.btnWriteFlashOn).setOnClickListener {
            mode = "WRITE"
            dataToWrite = "FLASH_ON"
            Toast.makeText(this, "Tap an NFC tag to write FLASH_ON", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnWriteFlashOff).setOnClickListener {
            mode = "WRITE"
            dataToWrite = "FLASH_OFF"
            Toast.makeText(this, "Tap an NFC tag to write FLASH_OFF", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnWriteUrl).setOnClickListener {
            mode = "WRITE"
            dataToWrite = "https://google.com"
            Toast.makeText(this, "Tap an NFC tag to write URL", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun turnFlash(enable: Boolean) {
        val cameraManager = getSystemService<CameraManager>()
        val cameraId = cameraManager?.cameraIdList?.first()
        cameraId?.let {
            cameraManager.setTorchMode(it, enable)
        }
    }

    private fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // 🔎 Log para depuración
        Log.d("NFC", "onNewIntent called! action=${intent.action}")
        Toast.makeText(this, "📡 NFC intent: ${intent.action}", Toast.LENGTH_SHORT).show()

        // ✅ Asegúrate de que realmente es un intent de NFC
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag == null) {
                Log.e("NFC", "No Tag found in intent")
                Toast.makeText(this, "⚠️ No NFC tag detected", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d("NFC", "Tag detected: $tag")

            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                val writable = ndef.isWritable
                val size = ndef.maxSize
                Log.d("NFC", "Writable: $writable, Capacity: $size bytes")
                ndef.close()

                Toast.makeText(this, "Writable: $writable, Size: $size bytes", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "This tag does not support NDEF", Toast.LENGTH_LONG).show()
            }

            // === WRITE MODE ===
            if (mode == "WRITE" && dataToWrite != null) {
                val success = NfcUtils.writeToTag(tag, dataToWrite!!)
                if (success) {
                    Toast.makeText(this, "✅ Tag written: $dataToWrite", Toast.LENGTH_LONG).show()
                    Log.d("NFC", "Tag written: $dataToWrite")
                } else {
                    Toast.makeText(this, "❌ Write failed!", Toast.LENGTH_LONG).show()
                    Log.e("NFC", "Tag write failed")
                }
                mode = null
                return
            }

            // === READ MODE ===
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val messages = rawMsgs?.map { it as NdefMessage }?.toTypedArray()
            val data = messages?.firstOrNull()?.let { NfcUtils.readFromMessage(it) }

            if (data != null) {
                Toast.makeText(this, "📖 Read: $data", Toast.LENGTH_LONG).show()
                Log.d("NFC", "Read from tag: $data")

                when (data.uppercase()) {
                    "FLASH_ON" -> turnFlash(true)
                    "FLASH_OFF" -> turnFlash(false)
                    else -> if (data.startsWith("http")) openUrl(data)
                }
            } else {
                Log.d("NFC", "No NDEF messages found")
                Toast.makeText(this, "⚠️ No readable data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w("NFC", "Received non-NFC intent: ${intent.action}")
        }
    }

}
