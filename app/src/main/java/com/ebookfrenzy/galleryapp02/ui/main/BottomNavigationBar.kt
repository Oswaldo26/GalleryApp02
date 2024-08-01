package com.ebookfrenzy.galleryapp02.ui.main


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Gallery,
        BottomNavItem.Maps,
        BottomNavItem.ScanQr,
        BottomNavItem.GalleryMaps,
        //BottomNavItem.Room,
        BottomNavItem.Painting

    )

    BottomNavigation(
        backgroundColor = Color.White, // Fondo blanco
        contentColor = Color.Black, // Color de contenido negro
        elevation = 8.dp // Añadir elevación
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        modifier = Modifier.size(if (isSelected) 35.dp else 24.dp) // Tamaño más grande si está seleccionado
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray,
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

