package com.ebookfrenzy.galleryapp02.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Gallery : BottomNavItem("Gallery", Icons.Filled.Home, "gallery")
    object Maps: BottomNavItem("Maps", Icons.Filled.Build,"maps")
    object Room : BottomNavItem("Room", Icons.Filled.List, "room")
    object ScanQr: BottomNavItem("ScanQr", Icons.Filled.FavoriteBorder,"scanqr")
    object GalleryMaps: BottomNavItem("GallMaps", Icons.Filled.FavoriteBorder,"galleryMaps")
    object Painting : BottomNavItem("Painting", Icons.Filled.Info, "painting")


}
