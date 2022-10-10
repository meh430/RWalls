package mp.redditwalls.design.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val RwTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 64.sp,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 52.sp,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 44.sp,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 40.sp,
        fontSize = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 32.sp,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RwFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        fontSize = 12.sp
    )
)