package mp.redditwalls.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.OptionsMenu
import mp.redditwalls.design.components.SearchBar
import mp.redditwalls.design.components.SubredditDetail
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.UiResult
import mp.redditwalls.preferences.enums.SortOrder
import mp.redditwalls.ui.components.ImagesList
import mp.redditwalls.utils.rememberSortMenuOptions
import mp.redditwalls.utils.toFriendlyCount
import mp.redditwalls.viewmodels.SearchImagesScreenViewModel

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SearchImagesScreen(
    vm: SearchImagesScreenViewModel = viewModel(),
    subreddit: String?,
    query: String?
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult.value

    Scaffold(
        topBar = {
            SearchBar(
                value = uiState.query.value,
                onValueChanged = { vm.onQueryChanged(it) },
                onSearch = {
                    keyboardController?.hide()
                    vm.fetchImages(true)
                },
                leadingIcon = {
                    BackButton(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = { (context as? Activity)?.finish() }
                    )
                },
                trailingIcon = {
                    if (uiState.query.value.isNotEmpty() && WindowInsets.isImeVisible) {
                        BackButton(
                            modifier = Modifier.padding(end = 16.dp),
                            cross = true,
                            onClick = { vm.onQueryChanged("") }
                        )
                    } else {
                        OptionsMenu(
                            modifier = Modifier.padding(end = 16.dp),
                            icon = Icons.Default.Sort,
                            options = rememberSortMenuOptions(context),
                            onOptionSelected = { vm.setSortOrder(SortOrder.values()[it]) }
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .consumedWindowInsets(it)
        ) {
            when {
                uiResult is UiResult.Error -> ErrorState(
                    errorMessage = uiResult.errorMessage.orEmpty(),
                    onRetryClick = { vm.fetchImages(true) }
                )
                uiResult is UiResult.Loading && uiState.images.isEmpty() -> Box {
                    ThreeDotsLoader(modifier = Modifier.align(Alignment.Center))
                }
                else -> ImagesList(
                    listState = vm.listState,
                    images = uiState.images,
                    isLoading = uiResult is UiResult.Loading,
                    onClick = {},
                    onImageLongPress = {},
                    onLikeClick = vm.favoriteImageViewModel::onLikeClick,
                    onLoadMore = { vm.fetchImages() },
                    header = uiState.subredditItemUiState.value?.let {
                        {
                            SubredditDetail(
                                modifier = Modifier.padding(8.dp),
                                description = it.description,
                                headerImageUrl = it.headerUrl,
                                iconImageUrl = it.subredditIconUrl,
                                subredditName = "r/${it.name}",
                                title = stringResource(
                                    R.string.subscriber_count,
                                    it.numSubscribers.toFriendlyCount()
                                ),
                                subTitle = stringResource(
                                    R.string.online_count,
                                    it.numOnline.toFriendlyCount()
                                ),
                                isSaved = it.isSaved.value,
                                onSaveChanged = { isSaved ->
                                    vm.savedSubredditViewModel.onSaveClick(it, isSaved)
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(subreddit, query) {
        vm.onScreenLaunched(
            subreddit = subreddit,
            q = query
        )
    }
}