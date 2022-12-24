package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.Image

@Composable
fun WallpaperScreen(arguments: WallpaperActivityArguments) {
    Image(
        modifier = Modifier.fillMaxSize(),
        imageUrl = arguments.imageUrl,
        showRipple = false
    )
}
