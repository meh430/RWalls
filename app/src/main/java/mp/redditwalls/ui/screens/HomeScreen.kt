package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date
import kotlinx.coroutines.launch
import mp.redditwalls.R
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.IconText
import mp.redditwalls.design.components.PopupMenu
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.design.components.WallpaperOptionsDialog
import mp.redditwalls.domain.models.DomainImageUrl
import mp.redditwalls.domain.models.DomainRecentActivityItem.DomainSetWallpaperActivityItem
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.UiResult
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.ui.components.ImagePager
import mp.redditwalls.ui.components.ImagesList
import mp.redditwalls.utils.launchBrowser
import mp.redditwalls.viewmodels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    vm: HomeScreenViewModel = viewModel(),
    wallpaperHelper: WallpaperHelper
) {
    val context = LocalContext.current
    val uiState = vm.uiState
    val uiResult = uiState.uiResult.value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var sortMenuExpanded by remember { mutableStateOf(false) }
    val sortMenuOptions = remember {
        SortOrder.values().map {
            IconText(text = context.getString(it.stringId))
        }
    }

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
                        Box {
                            IconButton(
                                onClick = { sortMenuExpanded = true }
                            ) {
                                Icon(imageVector = Icons.Default.Sort, contentDescription = null)
                            }
                            PopupMenu(
                                expanded = sortMenuExpanded,
                                options = sortMenuOptions,
                                onOptionSelected = {
                                    vm.setSortOrder(SortOrder.values()[it])
                                },
                                onDismiss = { sortMenuExpanded = false }
                            )
                        }
                    },
                )
            }
        },
        content = { innerPadding ->
            val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
            ) {
                WallpaperOptionsDialog(
                    show = uiState.longPressedImage.value != null,
                    onSelect = { selection ->
                        val index = uiState.longPressedImage.value
                        scope.launch {
                            index?.let {
                                val location = mp.redditwalls.WallpaperLocation.values()[selection]
                                wallpaperHelper.setImageAsWallpaper(
                                    context = context,
                                    imageUrl = it.imageUrl.highQualityUrl,
                                    location = location,
                                    recentActivityItem = it.run {
                                        DomainSetWallpaperActivityItem(
                                            dbId = 0,
                                            createdAt = Date(),
                                            subredditName = subredditName,
                                            domainImageUrl = DomainImageUrl(
                                                url = imageUrl.url,
                                                lowQualityUrl = imageUrl.lowQualityUrl,
                                                mediumQualityUrl = imageUrl.mediumQualityUrl,
                                                highQualityUrl = imageUrl.highQualityUrl
                                            ),
                                            imageNetworkId = networkId,
                                            wallpaperLocation = WallpaperLocation.values()[selection]
                                        )
                                    }
                                )
                            }
                        }
                    },
                    onDismiss = {
                        vm.setLongPressImage(null)
                    }
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
                            modifier = modifier,
                            images = uiState.images,
                            navigateToPost = { uiState.images[it].postUrl.launchBrowser(context) },
                            onImageSetWallpaperClick = vm::setLongPressImage,
                            onLoadMore = { vm.fetchHomeFeed() },
                            onLikeClick = vm::onLikeClick
                        )
                    }
                    !uiState.verticalSwipeFeedEnabled.value -> ImagesList(
                        modifier = modifier,
                        contentPadding = PaddingValues(8.dp),
                        images = uiState.images,
                        isLoading = uiResult is UiResult.Loading,
                        onClick = {},
                        onImageLongPress = vm::setLongPressImage,
                        onLikeClick = vm::onLikeClick,
                        onLoadMore = { vm.fetchHomeFeed() }
                    )
                }
            }
        }
    )
}