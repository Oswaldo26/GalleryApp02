package com.ebookfrenzy.galleryapp02.ui.qr
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.ebookfrenzy.galleryapp02.utils.Resource

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QrResultScreen(paintingId: String, viewModel: QrResultViewModel = hiltViewModel()) {
    val paintingState by viewModel.painting.collectAsState()
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var audioUrl by remember { mutableStateOf<String?>(null) }

    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    val headsetReceiver = rememberUpdatedState(object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state = intent?.getIntExtra("state", -1) ?: return
            if (state == 0) { // Headset disconnected
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    Toast.makeText(context, "Headphones disconnected, audio paused", Toast.LENGTH_SHORT).show()
                }
            }
        }
    })
    LaunchedEffect(paintingId) {
        viewModel.getPaintingById(paintingId)
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        context.registerReceiver(headsetReceiver.value, filter)
    }
    DisposableEffect(Unit) {
        onDispose {
            try {
                context.unregisterReceiver(headsetReceiver.value)
            } catch (e: IllegalArgumentException) {
                // Receiver was not registered
            }
        }
    }

    fun playAudio(url: String) {

        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val hasHeadphones = devices.any { device ->
            when (device.type) {
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                AudioDeviceInfo.TYPE_WIRED_HEADSET,
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLE_HEADSET,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> true
                else -> false
            }
        }

        if (hasHeadphones) {
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
        } else {
            Toast.makeText(context, "Please connect headphones to play audio", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold {
        when (val painting = paintingState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                painting.data?.let { painting ->
                    audioUrl = painting.audioUrl // Asignar la URL del audio
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(25.dp)
                    ) {
                        item {
                            Text(
                                text = painting.title,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = painting.artist,
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Image(
                                painter = rememberAsyncImagePainter(painting.imageUrl),
                                contentDescription = painting.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .padding(bottom = 16.dp)
                            )
                            Text(
                                text = painting.description,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = {
                                    audioUrl?.let { url ->
                                        playAudio(url)
                                    } ?: run {
                                        Toast.makeText(context, "Audio URL not found", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Reproducir Audio")
                            }
                            Spacer(modifier = Modifier.height(80.dp)) // Espacio adicional para evitar que el botón quede oculto
                        }
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Painting not found")
                    }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = painting.message ?: "An error occurred")
                }
            }
        }
    }
}
