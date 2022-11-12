package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.FilterChipBar
import mp.redditwalls.design.components.IconText
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.ImagesList
import mp.redditwalls.viewmodels.FavoriteImagesScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FavoriteImagesScreen(
    modifier: Modifier = Modifier,
    favoriteImagesScreenViewModel: FavoriteImagesScreenViewModel = viewModel(),
    wallpaperHelper: WallpaperHelper
) {
    val uiState = favoriteImagesScreenViewModel.favoriteImagesScreenUiState
    val uiResult = uiState.uiResult.value

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.favorites),
                        )
                    }
                },
                navigationIcon = {},
                actions = {

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            when (uiResult) {
                is UiResult.Error -> {
                    ErrorState(
                        errorMessage = uiResult.errorMessage
                            ?: stringResource(id = R.string.error_state_title),
                        onRetryClick = {
                            favoriteImagesScreenViewModel.setFilter(uiState.filter.value)
                        }
                    )
                }
                else -> {
                    FilterChipBar(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        filters = listOf(
                            stringResource(R.string.home) to Icons.Default.Home,
                            stringResource(R.string.lock) to Icons.Default.Lock,
                            stringResource(R.string.both) to Icons.Default.Smartphone
                        ).map { IconText(it.first, it.second) },
                        initialSelection = uiState.filter.value.ordinal,
                        onSelectionChanged = {
                            favoriteImagesScreenViewModel.setFilter(
                                WallpaperLocation.values()[it]
                            )
                        }
                    )
                    if (uiState.images.isEmpty() && uiResult is UiResult.Success) {
                        EmptyState()
                    } else {
                        ImagesList(
                            modifier = Modifier,
                            images = uiState.images,
                            isLoading = uiResult is UiResult.Loading,
                            onImageLongPress = {},
                            onLikeClick = favoriteImagesScreenViewModel::onLikeClick,
                            onLoadMore = {}
                        )
                    }
                }
            }
        }
    }
}