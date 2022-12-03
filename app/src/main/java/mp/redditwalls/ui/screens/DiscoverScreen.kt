package mp.redditwalls.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.design.components.DiscoverSubredditCard
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.SearchBar
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.RecentActivityItem
import mp.redditwalls.models.RecommendedSubredditUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageCardModel
import mp.redditwalls.viewmodels.DiscoverScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoverScreen(vm: DiscoverScreenViewModel = viewModel()) {
    val uiState = vm.discoverScreenUiState
    val uiResult = uiState.uiResult.value
    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier.clickable {},
                enabled = false,
                hint = stringResource(R.string.search)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
            ) {
                when (uiResult) {
                    is UiResult.Error -> ErrorState(
                        errorMessage = uiResult.errorMessage.orEmpty(),
                        onRetryClick = null
                    )
                    is UiResult.Loading -> ThreeDotsLoader()
                    is UiResult.Success -> {
                        DiscoverScreenContent(
                            recommendations = uiState.recommendedSubreddits,
                            recentActivity = uiState.recentActivityItems
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun DiscoverScreenContent(
    recommendations: List<RecommendedSubredditUiState>,
    recentActivity: List<RecentActivityItem>
) {
    LazyColumn {
        items(recommendations, key = { it.subredditName }) {
            DiscoverSubredditCard(
                subredditIconUrl = it.subredditIconUrl,
                subredditName = it.subredditName,
                subscriberCount = it.subscriberCount,
                isSaved = it.isSaved.value,
                imageCardModels = it.images.map { image ->
                    image.toImageCardModel(
                        onClick = {},
                        onLikeClick = {},
                        onLongPress = {}
                    )
                },
                onSaveChanged = {}
            )
        }
    }
}
