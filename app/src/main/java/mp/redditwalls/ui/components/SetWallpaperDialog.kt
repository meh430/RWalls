package mp.redditwalls.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.components.WallpaperOptionsDialog
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toDomainWallpaperRecentActivityItem

@Composable
fun SetWallpaperDialog(
    wallpaperHelper: WallpaperHelper,
    context: Context,
    image: ImageItemUiState?,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    WallpaperOptionsDialog(
        show = image != null,
        onSelect = { selection ->
            scope.launch {
                image?.let {
                    val location = mp.redditwalls.WallpaperLocation.values()[selection]
                    wallpaperHelper.setImageAsWallpaper(
                        context = context,
                        imageUrl = it.imageUrl.highQualityUrl,
                        location = location,
                        recentActivityItem = it.toDomainWallpaperRecentActivityItem(
                            location = WallpaperLocation.values()[selection]
                        )
                    )
                }
            }
        },
        onDismiss = onDismiss
    )
}