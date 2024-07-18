package com.ebookfrenzy.galleryapp02.ui.room



import android.app.Application
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ebookfrenzy.galleryapp02.beacon.BeaconScanner
import com.ebookfrenzy.galleryapp02.data.model.RoomModel
import com.ebookfrenzy.galleryapp02.data.repository.GalleryMapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val repository: GalleryMapsRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> get() = _scanResults

    private val beaconScanner = BeaconScanner(application)

    private val _room = MutableStateFlow<RoomModel?>(null)
    val room: StateFlow<RoomModel?> get() = _room

    fun startScanning() {
        viewModelScope.launch {
            Log.d("RoomViewModel", "Starting beacon scanning...")
            beaconScanner.startScanning {
                _scanResults.value = it
                Log.d("RoomViewModel", "Beacon scan results: $it")
            }
        }
    }

    fun stopScanning() {
        Log.d("RoomViewModel", "Stopping beacon scanning...")
        beaconScanner.stopScanning()
    }

    fun calculateUserPosition(): StateFlow<Offset?> {
        val knownBeacons: Map<String, Offset> = mapOf(
            "beacon1" to Offset(100f, 100f), // Coordenadas del beacon 1
            "beacon2" to Offset(300f, 100f), // Coordenadas del beacon 2
            "beacon3" to Offset(200f, 300f)  // Coordenadas del beacon 3
        )

        val userPosition = MutableStateFlow<Offset?>(null)

        viewModelScope.launch {
            val distances = scanResults.value.mapNotNull { result ->
                val position = knownBeacons[result.device.address]
                val txPower = beaconScanner.extractTxPower(result) ?: return@mapNotNull null
                val distance = calculateDistance(txPower, result.rssi)
                if (position != null && distance != null) {
                    Triple(position, distance, result.device.address)
                } else {
                    null
                }
            }

            if (distances.size < 3) return@launch

            // Implementar trilateración simple (esto es solo un ejemplo básico)
            val (p1, d1, _) = distances[0]
            val (p2, d2, _) = distances[1]
            val (p3, d3, _) = distances[2]

            val A = 2 * (p2.x - p1.x)
            val B = 2 * (p2.y - p1.y)
            val C = d1.pow(2) - d2.pow(2) - p1.x.pow(2) + p2.x.pow(2) - p1.y.pow(2) + p2.y.pow(2)
            val D = 2 * (p3.x - p2.x)
            val E = 2 * (p3.y - p2.y)
            val F = d2.pow(2) - d3.pow(2) - p2.x.pow(2) + p3.x.pow(2) - p2.y.pow(2) + p3.y.pow(2)

            val x = (C * E - F * B) / (E * A - B * D)
            val y = (C * D - A * F) / (B * D - A * E)

            userPosition.value = Offset(x, y)
            Log.d("RoomViewModel", "Calculated user position: $x, $y")
        }

        return userPosition
    }

    private fun calculateDistance(txPower: Int, rssi: Int): Float? {
        if (rssi == 0) {
            return null // No se puede calcular la distancia si el RSSI es 0
        }
        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            ratio.pow(10).toFloat()
        } else {
            (0.89976 * ratio.pow(7.7095) + 0.111).toFloat()
        }
    }

    fun getRoomById(galleryId: String, roomId: String) {
        viewModelScope.launch {
            val gallery = repository.fetchGallery(galleryId)
            _room.value = gallery?.rooms?.get(roomId)
        }
    }
}
