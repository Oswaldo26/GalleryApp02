package com.ebookfrenzy.galleryapp02.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ebookfrenzy.galleryapp02.ui.theme.GalleryApp02Theme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MuseumApp() {
    val navController = rememberNavController()
    GalleryApp02Theme {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            NavigationHost(navController)
        }
    }
}