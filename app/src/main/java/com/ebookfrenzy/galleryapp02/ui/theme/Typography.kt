package com.ebookfrenzy.galleryapp02.ui.theme



import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CustomTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = QuicksandFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = QuicksandFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    // Añadir más estilos de texto aquí si es necesario
)
