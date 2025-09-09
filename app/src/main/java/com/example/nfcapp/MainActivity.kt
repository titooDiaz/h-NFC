package com.example.nfcapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nfcapp.ui.theme.NFCAppTheme

//  Flash!
import android.hardware.camera2.CameraManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NFCAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlashlightButton(context = this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun FlashlightButton(context: Context) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    var isFlashOn = false

    Button(onClick = {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            isFlashOn = !isFlashOn
            cameraManager.setTorchMode(cameraId, isFlashOn)
            Toast.makeText(
                context,
                if (isFlashOn) "Flash ON" else "Flash OFF",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }) {
        Text(text = "Toggle Flashlight")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NFCAppTheme {
        Greeting("Android")
    }
}