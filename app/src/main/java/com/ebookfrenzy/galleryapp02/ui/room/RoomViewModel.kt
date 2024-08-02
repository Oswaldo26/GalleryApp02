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
import kotlinx.coroutines.flow.collectLatest
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

    private val _userPosition = MutableStateFlow<Offset?>(null)
    val userPosition: StateFlow<Offset?> get() = _userPosition

    fun startScanning() {
        viewModelScope.launch {
            Log.d("RoomViewModel", "Starting beacon scanning...")
            beaconScanner.startScanning {
                _scanResults.value = it
                Log.d("RoomViewModel", "Beacon scan results: $it")
                calculateUserPosition()
            }
        }
    }

    fun stopScanning() {
        Log.d("RoomViewModel", "Stopping beacon scanning...")
        beaconScanner.stopScanning()
    }

    private fun calculateUserPosition() {
        val knownBeacons: Map<String, Offset> = mapOf(
            "beacon1" to Offset(100f, 100f), // Coordenadas del beacon 1
            "beacon2" to Offset(300f, 100f), // Coordenadas del beacon 2
            "beacon3" to Offset(200f, 300f)  // Coordenadas del beacon 3
        )

        val distances = _scanResults.value.mapNotNull { result ->
            val position = knownBeacons[result.device.address]
            val txPower = beaconScanner.extractTxPower(result) ?: return@mapNotNull null
            val distance = calculateDistance(txPower, result.rssi)
            if (position != null && distance != null) {
                Triple(position, distance, result.device.address)
            } else {
                null
            }
        }

        if (distances.size < 3) return

        // Implementar trilateración real (esto es solo un ejemplo básico)
        val (p1, d1, _) = distances[0]
        val (p2, d2, _) = distances[1]
        val (p3, d3, _) = distances[2]

        // Coordenadas de los beacons
        val (x1, y1) = p1
        val (x2, y2) = p2
        val (x3, y3) = p3

        // Cálculo de la posición usando trilateración
        val a = 2 * (x2 - x1)
        val b = 2 * (y2 - y1)
        val c = d1.pow(2) - d2.pow(2) - x1.pow(2) + x2.pow(2) - y1.pow(2) + y2.pow(2)
        val d = 2 * (x3 - x2)
        val e = 2 * (y3 - y2)
        val f = d2.pow(2) - d3.pow(2) - x2.pow(2) + x3.pow(2) - y2.pow(2) + y3.pow(2)

        val x = (c * e - f * b) / (e * a - b * d)
        val y = (c * d - a * f) / (b * d - a * e)

        _userPosition.value = Offset(x, y)
        Log.d("RoomViewModelPosition", "Calculated user position: $x, $y")
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
