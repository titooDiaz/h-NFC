package com.example.nfcapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONObject

@Composable
fun MainScreen(onSelectAction: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select an action to write to NFC", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val json = JSONObject()
            json.put("action", "flashlight")
            onSelectAction(json.toString())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Toggle Flashlight")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val json = JSONObject()
            json.put("action", "open_url")
            json.put("url", "https://google.com")
            onSelectAction(json.toString())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Open Google.com")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val json = JSONObject()
            json.put("action", "google_home")
            json.put("device", "LivingRoomLight")
            json.put("command", "toggle")
            onSelectAction(json.toString())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Toggle Living Room Light")
        }
    }
}
