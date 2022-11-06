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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.ui.components.ImagePager
import mp.redditwalls.ui.components.ImagesList
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.IconText
import mp.redditwalls.design.components.PopupMenu
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toDomainImage
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.viewmodels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = homeScreenViewModel.homeScreenUiState
    val uiResult = uiState.uiResult.value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var sortMenuExpanded by remember { mutableStateOf(false) }
    val sortMenuOptions = remember {
        SortOrder.values().map {
            IconText(text = context.getString(it.stringId))
        }
    }

    val onLikeClick = { image: ImageItemUiState, isLiked: Boolean ->
        image.isLiked.value = isLiked
        if (isLiked) {
            homeScreenViewModel.addFavoriteImage(
                domainImage = image.toDomainImage(),
                refreshLocation = WallpaperLocation.BOTH
            )
        } else {
            homeScreenViewModel.removeFavoriteImage(image.dbId)
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
                                    homeScreenViewModel.setSortOrder(SortOrder.values()[it])
                                },
                                onDismiss = { sortMenuExpanded = false }
                            )
                        }
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
                when {
                    uiResult is UiResult.Error -> ErrorState(
                        errorMessage = uiResult.errorMessage.orEmpty()
                    ) {
                        homeScreenViewModel.fetchHomeFeed(true)
                    }
                    uiResult is UiResult.Loading && uiState.images.isEmpty() -> Box {
                        ThreeDotsLoader(modifier = Modifier.align(Alignment.Center))
                    }
                    uiResult is UiResult.Success && uiState.images.isEmpty() -> EmptyState()
                    uiState.verticalSwipeFeedEnabled.value -> {
                        ImagePager(
                            modifier = modifier,
                            images = uiState.images,
                            onLoadMore = { homeScreenViewModel.fetchHomeFeed() },
                            onLikeClick = onLikeClick
                        )
                    }
                    !uiState.verticalSwipeFeedEnabled.value -> ImagesList(
                        modifier = modifier,
                        contentPadding = PaddingValues(8.dp),
                        images = uiState.images,
                        isLoading = uiResult is UiResult.Loading,
                        onLikeClick = onLikeClick,
                        onLoadMore = { homeScreenViewModel.fetchHomeFeed() }
                    )
                }
            }
        }
    )
}