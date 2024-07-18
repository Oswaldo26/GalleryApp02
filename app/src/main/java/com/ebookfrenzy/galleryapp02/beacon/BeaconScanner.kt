package com.ebookfrenzy.galleryapp02.beacon

import android.os.ParcelUuid
import java.util.UUID


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.Activity
import android.util.Log

class BeaconScanner(private val context: Context) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val scanner = bluetoothAdapter?.bluetoothLeScanner
    private val handler = Handler(Looper.getMainLooper())

    private var scanCallback: ScanCallback? = null

    fun startScanning(onScanResult: (List<ScanResult>) -> Unit) {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        val scanResults = mutableListOf<ScanResult>()
        val filters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB"))) // UUID del servicio
                .build()
        )

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                scanResults.add(result)
                onScanResult(scanResults)
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)
                scanResults.addAll(results)
                onScanResult(scanResults)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e("BeaconScanner", "Scan failed: $errorCode")
            }
        }

        try {
            scanner?.startScan(filters, settings, scanCallback)
            Log.d("BeaconScanner", "Beacon scanning started successfully")
           // Escanea por 10 segundos y luego detén
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e("BeaconScanner", "SecurityException: ${e.message}")
            // Manejar la excepción de seguridad
        }
    }

    fun stopScanning() {
        try {
            scanCallback?.let {
                if (checkPermissions()) {
                    scanner?.stopScan(it)
                    Log.d("BeaconScanner", "Beacon scanning stopped")
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e("BeaconScanner", "SecurityException: ${e.message}")
            // Manejar la excepción de seguridad
        }
    }

    private fun checkPermissions(): Boolean {
        val permissions = listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val activity = context as? Activity
        activity?.let {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            ActivityCompat.requestPermissions(it, permissions, 1)
        }
    }

    fun extractTxPower(scanResult: ScanResult): Int? {
        val manufacturerData = scanResult.scanRecord?.getManufacturerSpecificData(0x004C)
        return manufacturerData?.let {
            if (it.size >= 2) it[1].toInt() else null
        }
    }
}
