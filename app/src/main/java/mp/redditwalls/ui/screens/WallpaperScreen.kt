package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.ImageAlbum
import mp.redditwalls.viewmodels.WallpaperScreenViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WallpaperScreen(
    vm: WallpaperScreenViewModel = viewModel(),
    arguments: WallpaperActivityArguments
) {
    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult

    val imageUrls = remember(uiState.image.imageUrls) {
        uiState.image.imageUrls.map { it.url }
    }

    ImageAlbum(
        modifier = Modifier.fillMaxSize(),
        imageUrls = imageUrls
    )

    LaunchedEffect(Unit) {
        vm.fetchWallpaper(arguments.imageNetworkId)
    }
}
