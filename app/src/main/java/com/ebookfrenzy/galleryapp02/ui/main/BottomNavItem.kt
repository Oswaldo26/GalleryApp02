package com.ebookfrenzy.galleryapp02.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.ebookfrenzy.galleryapp02.R


sealed class BottomNavItem(@DrawableRes var icon: Int, var route: String) {
    object Gallery : BottomNavItem(R.drawable.home_icon, "gallery")
    object Maps: BottomNavItem(R.drawable.maps_icon,"maps")
    object Room : BottomNavItem(R.drawable.maps_icon, "room")
    object ScanQr: BottomNavItem(R.drawable.scanqr_icon,"scanqr")

    object GalleryMaps: BottomNavItem( R.drawable.gallmaps_icon,"galleryMaps")
    object Painting : BottomNavItem(R.drawable.paint_icon, "painting")


}
