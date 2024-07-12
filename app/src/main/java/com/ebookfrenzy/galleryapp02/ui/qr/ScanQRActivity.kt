package com.ebookfrenzy.galleryapp02.ui.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ScanQrScsreen(navController: NavController) {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val barcodeView = remember { CompoundBarcodeView(context) }
        AndroidView({ barcodeView }) { view ->
            view.decodeContinuous(object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult) {
                    scanResult = result.text
                }

                override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {}
            })
        }
        if (scanResult.isNotEmpty()) {
            Text(text = "QR Code: $scanResult")
        }
    }
}