package mp.redditwalls.design.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import mp.redditwalls.design.R

val RwFontFamily = FontFamily(
    listOf(
        Font(resId = R.font.rubik_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resId = R.font.rubik_light, weight = FontWeight.Light, style = FontStyle.Normal),
        Font(resId = R.font.rubik_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resId = R.font.rubik_bold, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resId = R.font.rubik_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal)
    )
)