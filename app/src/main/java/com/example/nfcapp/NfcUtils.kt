package com.example.nfcapp

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import java.nio.charset.Charset

object NfcUtils {

    /**
     * Write text data to NFC tag
     */
    fun writeToTag(tag: Tag, data: String): Boolean {
        return try {
            val ndef = Ndef.get(tag) ?: return false
            ndef.connect()

            val record = createTextRecord(data)
            val message = NdefMessage(arrayOf(record))

            ndef.writeNdefMessage(message)
            ndef.close()

            Log.d("NFC", "Successfully wrote: $data")
            true
        } catch (e: Exception) {
            Log.e("NFC", "Error writing to tag", e)
            false
        }
    }

    /**
     * Read data from NFC message
     */
    fun readFromMessage(message: NdefMessage): String? {
        return try {
            val record = message.records.firstOrNull() ?: return null

            // Check if it's a text record
            if (record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                record.type.contentEquals(NdefRecord.RTD_TEXT)) {

                val payload = record.payload
                val textEncoding = if ((payload[0].toInt() and 0x80) == 0) "UTF-8" else "UTF-16"
                val languageCodeLength = payload[0].toInt() and 0x3F

                String(
                    payload,
                    languageCodeLength + 1,
                    payload.size - languageCodeLength - 1,
                    Charset.forName(textEncoding)
                )
            } else {
                // Try to read as plain text
                String(record.payload, Charset.forName("UTF-8"))
            }
        } catch (e: Exception) {
            Log.e("NFC", "Error reading from message", e)
            null
        }
    }

    /**
     * Clear/erase NFC tag
     */
    fun clearTag(tag: Tag): Boolean {
        return try {
            val ndef = Ndef.get(tag) ?: return false
            ndef.connect()

            // Write empty message
            val empty = NdefMessage(
                NdefRecord(
                    NdefRecord.TNF_EMPTY,
                    byteArrayOf(),
                    byteArrayOf(),
                    byteArrayOf()
                )
            )

            ndef.writeNdefMessage(empty)
            ndef.close()

            Log.d("NFC", "Tag cleared successfully")
            true
        } catch (e: Exception) {
            Log.e("NFC", "Error clearing tag", e)
            false
        }
    }

    /**
     * Create a text NDEF record
     */
    private fun createTextRecord(text: String): NdefRecord {
        val languageCode = "en"
        val languageCodeBytes = languageCode.toByteArray(Charset.forName("US-ASCII"))
        val textBytes = text.toByteArray(Charset.forName("UTF-8"))

        val recordPayload = ByteArray(1 + languageCodeBytes.size + textBytes.size)
        recordPayload[0] = languageCodeBytes.size.toByte()

        System.arraycopy(languageCodeBytes, 0, recordPayload, 1, languageCodeBytes.size)
        System.arraycopy(textBytes, 0, recordPayload, 1 + languageCodeBytes.size, textBytes.size)

        return NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            byteArrayOf(),
            recordPayload
        )
    }
}