package mp.redditwalls.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.activities.SearchImagesActivity
import mp.redditwalls.activities.SearchImagesActivityArguments
import mp.redditwalls.activities.SearchSubredditsActivity
import mp.redditwalls.design.components.DiscoverSubredditCard
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.ImageFolderRadioDialog
import mp.redditwalls.design.components.SearchBar
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.RecentActivityItem
import mp.redditwalls.models.RecommendedSubredditUiState
import mp.redditwalls.models.SubredditItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageCardModel
import mp.redditwalls.ui.components.recentActivityListItems
import mp.redditwalls.utils.toFriendlyCount
import mp.redditwalls.viewmodels.DiscoverScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoverScreen(vm: DiscoverScreenViewModel = viewModel()) {
    val uiState = vm.uiState
    val uiResult = uiState.uiResult.value

    val context = LocalContext.current

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
            ) {
                when (uiResult) {
                    is UiResult.Error -> ErrorState(
                        errorMessage = uiResult.errorMessage.orEmpty(),
                        onRetryClick = vm::getDiscoverFeed
                    )
                    is UiResult.Loading -> ThreeDotsLoader()
                    is UiResult.Success -> {
                        DiscoverScreenContent(
                            recommendations = uiState.recommendedSubreddits,
                            recentActivity = uiState.recentActivityItems,
                            usePresetFolderWhenLiking = uiState.usePresetFolderWhenLiking.value,
                            folderNames = uiState.folderNames,
                            onSearchClick = {
                                SearchSubredditsActivity.launch(context)
                            },
                            onSaveClick = vm.savedSubredditViewModel::onSaveClick,
                            onLikeClick = vm.favoriteImageViewModel::onLikeClick
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun DiscoverScreenContent(
    recommendations: List<RecommendedSubredditUiState>,
    recentActivity: List<RecentActivityItem>,
    folderNames: List<String>,
    usePresetFolderWhenLiking: Boolean,
    onSearchClick: () -> Unit,
    onSaveClick: (sub: SubredditItemUiState, isSaved: Boolean) -> Unit,
    onLikeClick: (image: ImageItemUiState, isLiked: Boolean, folder: String?) -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    var showFolderSelectDialog: ImageItemUiState? by remember { mutableStateOf(null) }
    ImageFolderRadioDialog(
        show = showFolderSelectDialog != null,
        options = folderNames,
        onSubmit = { name ->
            showFolderSelectDialog?.let {
                onLikeClick(
                    it,
                    !it.isLiked.value,
                    name
                )
            }
        },
        onDismiss = { showFolderSelectDialog = null }
    )
    LazyColumn {
        item {
            SearchBar(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onSearchClick
                ),
                enabled = false
            )
        }
        if (recommendations.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
                    text = stringResource(id = R.string.recommendations),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(recommendations) {
            DiscoverSubredditCard(
                subredditIconUrl = it.subredditItemUiState.subredditIconUrl,
                subredditName = "r/${it.subredditItemUiState.name}",
                subscriberCount = "${it.subredditItemUiState.numSubscribers.toFriendlyCount()} subscribers",
                isSaved = it.subredditItemUiState.isSaved.value,
                imageCardModels = it.images.map { image ->
                    image.toImageCardModel(
                        onClick = {},
                        onLikeClick = { isLiked ->
                            if (usePresetFolderWhenLiking || !isLiked) {
                                onLikeClick(image, isLiked, null)
                            } else {
                                showFolderSelectDialog = image
                            }
                        },
                        onLongPress = {}
                    )
                },
                onSaveChanged = { isSaved -> onSaveClick(it.subredditItemUiState, isSaved) },
                onClick = {
                    SearchImagesActivity.launch(
                        context,
                        SearchImagesActivityArguments(
                            subreddit = it.subredditItemUiState.name
                        )
                    )
                }
            )
        }

        if (recentActivity.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.recent_activity),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(
                            text = stringResource(id = R.string.view_more),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        recentActivityListItems(
            context = context,
            recentActivityItems = recentActivity
        )
    }
}
