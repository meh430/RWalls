package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import mp.redditwalls.R
import mp.redditwalls.WallpaperHelper
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
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        favoriteImagesScreenViewModel.setFilter(WallpaperLocation.HOME)
    }

    val uiState = favoriteImagesScreenViewModel.favoriteImagesScreenUiState
    val uiResult = uiState.uiResult.value

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val topBarColor = if (uiState.selecting.value) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    systemUiController.setSystemBarsColor(topBarColor)
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.selecting.value) {
                                stringResource(
                                    R.string.selected_count,
                                    uiState.selectedCount.value
                                )
                            } else {
                                stringResource(R.string.favorites)
                            },
                        )
                    }
                },
                navigationIcon = {
                    if (uiState.selecting.value) {
                        IconButton(
                            onClick = { favoriteImagesScreenViewModel.stopSelecting() }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                actions = {

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = topBarColor
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
                    ImagesList(
                        modifier = Modifier,
                        images = uiState.images,
                        isLoading = uiResult is UiResult.Loading,
                        onClick = {
                            if (uiState.selecting.value) {
                                favoriteImagesScreenViewModel.selectImage(it)
                            }
                        },
                        onImageLongPress = {
                            if (!uiState.selecting.value) {
                                favoriteImagesScreenViewModel.selectImage(it)
                            }
                        },
                        onLikeClick = favoriteImagesScreenViewModel::onLikeClick,
                        onLoadMore = {},
                        header = {
                            FilterChipBar(
                                modifier = Modifier.padding(horizontal = 4.dp),
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
                        }
                    )
                }
            }
        }
    }
}