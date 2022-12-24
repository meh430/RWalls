package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.ImageAlbum

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WallpaperScreen(arguments: WallpaperActivityArguments) {
    ImageAlbum(
        modifier = Modifier.fillMaxSize(),
        imageUrls = List (5) { arguments.imageUrl }
    )
}
