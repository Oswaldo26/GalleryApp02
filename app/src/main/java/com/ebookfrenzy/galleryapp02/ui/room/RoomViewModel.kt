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

            when (distances.size) {
                1 -> {
                    // Caso de un solo beacon: usar estimación de proximidad
                    val (p1, d1, _) = distances[0]
                    userPosition.value = p1.copy(y = p1.y + d1) // Esto es solo un ejemplo, ajustar según sea necesario
                }
                2 -> {
                    // Caso de dos beacons: intersección de dos círculos
                    val (p1, d1, _) = distances[0]
                    val (p2, d2, _) = distances[1]
                    // Aquí puedes hacer una aproximación en el eje x o y
                    val x = (p1.x + p2.x) / 2 // Esto es solo un ejemplo, ajustar según sea necesario
                    val y = (p1.y + p2.y) / 2 // Esto es solo un ejemplo, ajustar según sea necesario
                    userPosition.value = Offset(x, y)
                }
                3 -> {
                    // Caso de tres beacons: trilateración completa
                    val (p1, d1, _) = distances[0]
                    val (p2, d2, _) = distances[1]
                    val (p3, d3, _) = distances[2]

                    val x = calculateTrilaterationX(p1, d1, p2, d2, p3, d3)
                    val y = calculateTrilaterationY(p1, d1, p2, d2, p3, d3)

                    userPosition.value = Offset(x, y)
                }
                else -> {
                    // Caso de más de tres beacons: usar los tres más cercanos
                    if (distances.size > 3) {
                        val sortedDistances = distances.sortedBy { it.second }
                        val (p1, d1, _) = sortedDistances[0]
                        val (p2, d2, _) = sortedDistances[1]
                        val (p3, d3, _) = sortedDistances[2]

                        val x = calculateTrilaterationX(p1, d1, p2, d2, p3, d3)
                        val y = calculateTrilaterationY(p1, d1, p2, d2, p3, d3)

                        userPosition.value = Offset(x, y)
                    }
                }
            }
            Log.d("RoomViewModelPosition", "Calculated user position: ${userPosition.value}")
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

    private fun calculateTrilaterationX(p1: Offset, d1: Float, p2: Offset, d2: Float, p3: Offset, d3: Float): Float {
        val A = 2 * (p2.x - p1.x)
        val B = 2 * (p2.y - p1.y)
        val C = d1.pow(2) - d2.pow(2) - p1.x.pow(2) + p2.x.pow(2) - p1.y.pow(2) + p2.y.pow(2)
        val D = 2 * (p3.x - p2.x)
        val E = 2 * (p3.y - p2.y)
        val F = d2.pow(2) - d3.pow(2) - p2.x.pow(2) + p3.x.pow(2) - p2.y.pow(2) + p3.y.pow(2)

        return (C * E - F * B) / (E * A - B * D)
    }

    private fun calculateTrilaterationY(p1: Offset, d1: Float, p2: Offset, d2: Float, p3: Offset, d3: Float): Float {
        val A = 2 * (p2.x - p1.x)
        val B = 2 * (p2.y - p1.y)
        val C = d1.pow(2) - d2.pow(2) - p1.x.pow(2) + p2.x.pow(2) - p1.y.pow(2) + p2.y.pow(2)
        val D = 2 * (p3.x - p2.x)
        val E = 2 * (p3.y - p2.y)
        val F = d2.pow(2) - d3.pow(2) - p2.x.pow(2) + p3.x.pow(2) - p2.y.pow(2) + p3.y.pow(2)

        return (C * D - A * F) / (B * D - A * E)
    }

    fun getRoomById(galleryId: String, roomId: String) {
        viewModelScope.launch {
            val gallery = repository.fetchGallery(galleryId)
            _room.value = gallery?.rooms?.get(roomId)
        }
    }

    private val knownBeacons: Map<String, Offset> = mapOf(
        "beacon1" to Offset(100f, 100f), // Coordenadas del beacon 1
        "beacon2" to Offset(300f, 100f), // Coordenadas del beacon 2
        "beacon3" to Offset(200f, 300f)  // Coordenadas del beacon 3
    )
}
