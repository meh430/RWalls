package mp.redditwalls.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.ImageAlbum
import mp.redditwalls.design.components.PageIndicator
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.WallpaperInfoCard
import mp.redditwalls.viewmodels.WallpaperScreenViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WallpaperScreen(
    vm: WallpaperScreenViewModel = viewModel(),
    arguments: WallpaperActivityArguments
) {
    val context = LocalContext.current
    val statusBarHeight =
        WindowInsets.systemBarsIgnoringVisibility.asPaddingValues().calculateTopPadding()

    val pagerState = rememberPagerState()
    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult

    val imageUrls = remember(uiState.image.imageUrls) {
        uiState.image.imageUrls.map { it.url }
    }

    var expanded by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            when (uiResult) {
                is UiResult.Error -> ErrorState(errorMessage = uiResult.errorMessage.orEmpty()) {
                    vm.fetchWallpaper(arguments.imageNetworkId)
                }
                is UiResult.Loading -> ThreeDotsLoader()
                is UiResult.Success -> {
                    ImageAlbum(
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState,
                        imageUrls = imageUrls
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = statusBarHeight,
                                horizontal = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BackButton(
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.surface.copy(0.7f),
                                CircleShape
                            ),
                            tint = MaterialTheme.colorScheme.onSurface,
                            onClick = { (context as? Activity)?.finish() }
                        )
                        if (pagerState.pageCount > 1) {
                            PageIndicator(state = pagerState)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        WallpaperInfoCard(
                            folderName = uiState.folderName,
                            image = uiState.image,
                            subreddit = uiState.subreddit,
                            expanded = expanded,
                            onExpand = { expanded = !expanded },
                            navigateToPost = {},
                            navigateToUser = {}
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.fetchWallpaper(arguments.imageNetworkId)
    }
}
