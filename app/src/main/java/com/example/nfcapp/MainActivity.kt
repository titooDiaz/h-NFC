package com.example.nfcapp

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import android.nfc.tech.Ndef
import android.util.Log
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var mode: String? = null // "WRITE" or "READ"
    private var dataToWrite: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Button to write "FLASH_ON" - NOW IT'S A CARD
        findViewById<MaterialCardView>(R.id.btnWriteFlashOn).setOnClickListener {
            mode = "WRITE"
            dataToWrite = "FLASH_ON"
            Toast.makeText(this, "Tap an NFC tag to write FLASH_ON", Toast.LENGTH_SHORT).show()
        }

        // Button to write "FLASH_OFF" - NOW IT'S A CARD
        findViewById<MaterialCardView>(R.id.btnWriteFlashOff).setOnClickListener {
            mode = "WRITE"
            dataToWrite = "FLASH_OFF"
            Toast.makeText(this, "Tap an NFC tag to write FLASH_OFF", Toast.LENGTH_SHORT).show()
        }

        // Button to write a URL - NOW IT'S A CARD
        findViewById<MaterialCardView>(R.id.btnWriteUrl).setOnClickListener {
            mode = "WRITE"
            dataToWrite = "https://google.com"
            Toast.makeText(this, "Tap an NFC tag to write URL", Toast.LENGTH_SHORT).show()
        }

        // Clear button - THIS ONE IS STILL A BUTTON
        findViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            Toast.makeText(this, "Clear mode activated", Toast.LENGTH_SHORT).show()
            mode = "CLEAR"
        }

        // Button to turn on your bulb
        findViewById<MaterialCardView>(R.id.btnTurnBulbOn).setOnClickListener {
            Toast.makeText(this, "Encendiendo bombillo...", Toast.LENGTH_SHORT).show()
            sendBulbCommand(true)
        }

        // Handle NFC intent if the app was launched by NFC
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null) return

        // Check for data passed by NfcDispatcherActivity
        val directData = intent.getStringExtra("nfc_data")
        if (directData != null) {
            Log.d("NFC", "App launched with NFC data: $directData")
            actOnNfcData(directData)
            return
        }

        // Normal NFC system intents
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {

            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val messages = rawMsgs?.map { it as NdefMessage }?.toTypedArray()
            val data = messages?.firstOrNull()?.let { NfcUtils.readFromMessage(it) }

            if (data != null) {
                actOnNfcData(data)
            } else {
                Log.d("NFC", "No NDEF messages found in tag")
            }
        }
    }

    // turn on the ligth
    private fun sendBulbCommand(turnOn: Boolean) {
        val deviceId = "ebca2a9934f770d5a2lplu"
        val command = if (turnOn) "turnOn" else "turnOff"

        Log.d("BULB", "Comando enviado: $command al dispositivo $deviceId")
        Toast.makeText(this, "✅ Bombillo encendido", Toast.LENGTH_SHORT).show()
    }


    private fun actOnNfcData(data: String) {
        Log.d("NFC", "Executing NFC action: $data")
        Toast.makeText(this, "NFC Action: $data", Toast.LENGTH_SHORT).show()

        when (data.uppercase()) {
            "FLASH_ON" -> turnFlash(true)
            "FLASH_OFF" -> turnFlash(false)
            else -> if (data.startsWith("http")) openUrl(data)
        }
    }

    override fun onResume() {
        super.onResume()

        // Pending intent to capture NFC intents while the app is in the foreground
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        // Filters for NFC intents
        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            try {
                addDataType("*/*") // Accepts any MIME type
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Failed to add MIME type", e)
            }
        }

        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        val tag = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        val filters = arrayOf(ndef, tech, tag)
        val techList = arrayOf(arrayOf(Ndef::class.java.name))

        // Enable foreground dispatch to capture NFC intents
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techList)
    }

    override fun onPause() {
        super.onPause()
        // Disable foreground dispatch when activity is paused
        nfcAdapter?.disableForegroundDispatch(this)
    }

    // Toggle flashlight ON or OFF
    private fun turnFlash(enable: Boolean) {
        val cameraManager = getSystemService<CameraManager>()
        val cameraId = cameraManager?.cameraIdList?.first()
        cameraId?.let {
            cameraManager.setTorchMode(it, enable)
        }
    }

    // Open URL in default browser
    private fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)

        // Debug log
        Log.d("NFC", "onNewIntent called! action=${intent.action}")
        Toast.makeText(this, "📡 NFC intent: ${intent.action}", Toast.LENGTH_SHORT).show()

        // Make sure it's really an NFC intent
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

            // Check if tag is NDEF-capable
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

            // === CLEAR MODE ===
            if (mode == "CLEAR") {
                val success = NfcUtils.clearTag(tag)
                if (success) {
                    Toast.makeText(this, "✅ Tag cleared", Toast.LENGTH_LONG).show()
                    Log.d("NFC", "Tag cleared successfully")
                } else {
                    Toast.makeText(this, "❌ Clear failed!", Toast.LENGTH_LONG).show()
                    Log.e("NFC", "Tag clear failed")
                }
                mode = null
                return
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

                // Act based on the content
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