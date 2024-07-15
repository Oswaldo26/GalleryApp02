package com.ebookfrenzy.galleryapp02.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ebookfrenzy.galleryapp02.ui.gallery.GalleryMapsScreen
import com.ebookfrenzy.galleryapp02.ui.gallery.GalleryScreen
import com.ebookfrenzy.galleryapp02.ui.maps.MapsScreen
import com.ebookfrenzy.galleryapp02.ui.painting.PaintingDetailScreen
import com.ebookfrenzy.galleryapp02.ui.painting.PaintingScreen
import com.ebookfrenzy.galleryapp02.ui.room.RoomScreen
import com.ebookfrenzy.galleryapp02.ui.qr.ScanQrScreen


@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Gallery.route) {
        composable(BottomNavItem.GalleryMaps.route) {
            GalleryMapsScreen(onRoomClick = { roomId ->
                navController.navigate("room/$roomId")
            })
        }
        composable(BottomNavItem.Gallery.route) { GalleryScreen() }
        composable(BottomNavItem.Maps.route){ MapsScreen() }
        composable(BottomNavItem.ScanQr.route){ ScanQrScreen(navController) }
        composable(BottomNavItem.Room.route) { RoomScreen(roomId = "",roomHeight = 500.dp) }
        composable("room/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            roomId?.let { RoomScreen(roomId = it) }
        }
        composable(BottomNavItem.Painting.route) { PaintingScreen(navController) }
        composable("paintingDetail/{paintingId}") { backStackEntry ->
            val paintingId = backStackEntry.arguments?.getString("paintingId")
            paintingId?.let { PaintingDetailScreen(paintingId = it) }
        }


    }
}