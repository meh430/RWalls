package mp.redditwalls.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.ImageAlbum
import mp.redditwalls.design.components.PageIndicator
import mp.redditwalls.viewmodels.WallpaperScreenViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WallpaperScreen(
    vm: WallpaperScreenViewModel = viewModel(),
    arguments: WallpaperActivityArguments
) {
    val context = LocalContext.current

    val pagerState = rememberPagerState()
    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult

    val imageUrls = remember(uiState.image.imageUrls) {
        uiState.image.imageUrls.map { it.url }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            ImageAlbum(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                imageUrls = imageUrls
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .displayCutoutPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackButton(
                    tint = MaterialTheme.colorScheme.surface,
                    onClick = { (context as? Activity)?.finish() }
                )
                if (pagerState.pageCount > 1) {
                    PageIndicator(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        state = pagerState
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.fetchWallpaper(arguments.imageNetworkId)
    }
}
