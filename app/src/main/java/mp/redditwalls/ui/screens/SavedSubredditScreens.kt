package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
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
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.SubredditCard
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.UiResult
import mp.redditwalls.utils.toFriendlyCount
import mp.redditwalls.viewmodels.SavedSubredditsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SavedSubredditScreens(vm: SavedSubredditsScreenViewModel = viewModel()) {
    val uiResult = vm.savedSubredditsScreenUiState.uiResult
    val subreddits = vm.savedSubredditsScreenUiState.subreddits

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
                        Text(text = stringResource(R.string.saved_subreddits))
                    }
                },
                navigationIcon = {},
                actions = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            when (uiResult) {
                is UiResult.Error -> ErrorState(errorMessage = uiResult.errorMessage.orEmpty())
                is UiResult.Loading -> ThreeDotsLoader()
                is UiResult.Success -> LazyColumn(
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(subreddits) { subreddit ->
                        SubredditCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            subredditIconUrl = subreddit.subredditIconUrl,
                            subredditName = subreddit.name,
                            subscriberCount = stringResource(
                                R.string.subscriber_count,
                                subreddit.numSubscribers.toFriendlyCount()
                            ),
                            subredditDescription = subreddit.description,
                            isSaved = subreddit.isSaved.value,
                            onSaveChanged = {
                                vm.savedSubredditViewModel.onSaveClick(
                                    subreddit,
                                    it
                                )
                            },
                            onClick = {},
                            onLongPress = {}
                        )
                    }
                }
            }
        }
    }
}