package com.ebookfrenzy.galleryapp02.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ScanQrScreen(navController: NavController) {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf("") }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val mediaPlayer = remember { MediaPlayer() }
    var audioUrl by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(scanResult) {
        if (scanResult.isNotEmpty()) {
            audioUrl = scanResult // Almacenar el link del audio directamente desde el QR
           // message = "Link guardado"
        }
    }

    if (hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val barcodeView = remember { CompoundBarcodeView(context) }
            AndroidView({ barcodeView }) { view ->
                view.decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult) {
                        if (result.text != scanResult) {
                            scanResult = result.text
                            audioUrl = null // Reiniciar audioUrl para permitir la animación
                        }
                    }

                    override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {}
                })
                view.resume() // Iniciar el escaneo
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (scanResult.isNotEmpty()) {
                   // Text(text = "QR Code: $scanResult")
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(text = message)
                Spacer(modifier = Modifier.height(16.dp)) // Añadir un espacio fijo entre el mensaje y el botón

                AnimatedVisibility(
                    visible = audioUrl != null,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    if (audioUrl != null) {
                        Button(onClick = { playAudio(audioUrl!!, mediaPlayer, context) }) {
                            Text(text = "REPRODUCIR AUDIO QR")
                        }
                        Spacer(modifier = Modifier.height(32.dp)) // Espacio adicional debajo del botón
                    }
                }

                Spacer(modifier = Modifier.weight(3f)) // Empujar el contenido hacia arriba

                // Botón adicional para reproducir un audio específico de Firebase
                Button(onClick = { playAudio("https://firebasestorage.googleapis.com/v0/b/your-app-id.appspot.com/o/audio1.mp3?alt=media&token=some-token", mediaPlayer, context) }) {
                    Text(text = "Reproducir Audio Específico")
                }
            }
        }
    } else {
        Text(text = "Permissions not granted")
    }
}

fun playAudio(url: String, mediaPlayer: MediaPlayer, context: android.content.Context) {
    try {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
        mediaPlayer.prepareAsync()
    } catch (e: Exception) {
        Toast.makeText(context, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}