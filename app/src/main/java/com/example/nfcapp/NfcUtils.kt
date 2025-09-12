package com.example.nfcapp

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import java.nio.charset.Charset

object NfcUtils {

    // Write text to tag (will format if needed)
    fun writeToTag(tag: Tag, text: String): Boolean {
        val ndefMessage = createTextMessage(text)

        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) return false
                ndef.writeNdefMessage(ndefMessage)
                ndef.close()
                true
            } else {
                // Tag not formatted yet
                val format = NdefFormatable.get(tag)
                format?.connect()
                format?.format(ndefMessage)
                format?.close()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Clear tag (write empty message)
    fun clearTag(tag: Tag): Boolean {
        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) return false
                val emptyMsg = NdefMessage(arrayOf(NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)))
                ndef.writeNdefMessage(emptyMsg)
                ndef.close()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Create a text message
    private fun createTextMessage(text: String): NdefMessage {
        val lang = "en"
        val textBytes = text.toByteArray(Charset.forName("UTF-8"))
        val langBytes = lang.toByteArray(Charset.forName("US-ASCII"))
        val payload = ByteArray(1 + langBytes.size + textBytes.size)
        payload[0] = langBytes.size.toByte()
        System.arraycopy(langBytes, 0, payload, 1, langBytes.size)
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)

        val record = NdefRecord(NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT, ByteArray(0), payload)

        return NdefMessage(arrayOf(record))
    }

    // Read text message
    fun readFromMessage(message: NdefMessage): String? {
        val record = message.records.firstOrNull() ?: return null
        return try {
            val payload = record.payload
            val textEncoding = if ((payload[0].toInt() and 128) == 0) Charsets.UTF_8 else Charsets.UTF_16
            val langCodeLen = payload[0].toInt() and 63
            String(payload, langCodeLen + 1, payload.size - langCodeLen - 1, textEncoding)
        } catch (e: Exception) {
            null
        }
    }
}
