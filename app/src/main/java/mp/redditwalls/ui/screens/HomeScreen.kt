package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.OptionsMenu
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.UiResult
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.ui.components.ImagePager
import mp.redditwalls.ui.components.ImagesList
import mp.redditwalls.ui.components.SetWallpaperDialog
import mp.redditwalls.utils.launchBrowser
import mp.redditwalls.utils.rememberSortMenuOptions
import mp.redditwalls.viewmodels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    vm: HomeScreenViewModel = viewModel(),
    wallpaperHelper: WallpaperHelper
) {
    val context = LocalContext.current
    val uiState = vm.uiState
    val uiResult = uiState.uiResult.value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!uiState.verticalSwipeFeedEnabled.value) {
                CenterAlignedTopAppBar(
                    modifier = Modifier,
                    scrollBehavior = scrollBehavior,
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "RWalls",
                            )
                            Text(
                                text = uiState.sortOrder.value?.stringId?.let {
                                    stringResource(it).lowercase()
                                }.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    navigationIcon = {},
                    actions = {
                        OptionsMenu(
                            icon = Icons.Default.Sort,
                            options = rememberSortMenuOptions(context = context),
                            onOptionSelected = { vm.setSortOrder(SortOrder.values()[it]) }
                        )
                    },
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
            ) {
                SetWallpaperDialog(
                    wallpaperHelper = wallpaperHelper,
                    context = context,
                    image = uiState.longPressedImage.value,
                    onDismiss = { vm.setLongPressImage(null) }
                )
                when {
                    uiResult is UiResult.Error -> ErrorState(
                        modifier = Modifier.padding(12.dp),
                        errorMessage = uiResult.errorMessage
                            ?: stringResource(id = R.string.error_state_title)
                    ) {
                        vm.fetchHomeFeed(true)
                    }
                    uiResult is UiResult.Loading && uiState.images.isEmpty() -> Box {
                        ThreeDotsLoader(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.verticalSwipeFeedEnabled.value -> {
                        ImagePager(
                            images = uiState.images,
                            folderNames = uiState.folderNames,
                            usePresetFolderWhenLiking = uiState.usePresetFolderWhenLiking.value,
                            navigateToPost = { it.postUrl.launchBrowser(context) },
                            onImageSetWallpaperClick = vm::setLongPressImage,
                            onLoadMore = { vm.fetchHomeFeed() },
                            onLikeClick = vm.favoriteImageViewModel::onLikeClick
                        )
                    }
                    !uiState.verticalSwipeFeedEnabled.value -> ImagesList(
                        listState = vm.listState,
                        images = uiState.images,
                        isLoading = uiResult is UiResult.Loading,
                        folderNames = uiState.folderNames,
                        usePresetFolderWhenLiking = uiState.usePresetFolderWhenLiking.value,
                        onImageLongPress = vm::setLongPressImage,
                        onLikeClick = vm.favoriteImageViewModel::onLikeClick,
                        onLoadMore = { vm.fetchHomeFeed() }
                    )
                }
            }
        }
    )
}