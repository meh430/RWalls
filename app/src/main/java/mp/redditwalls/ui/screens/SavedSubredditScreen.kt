package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.activities.SearchImagesActivity
import mp.redditwalls.activities.SearchImagesActivityArguments
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.SubredditsList
import mp.redditwalls.viewmodels.SavedSubredditsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SavedSubredditScreen(vm: SavedSubredditsScreenViewModel = viewModel()) {
    val context = LocalContext.current

    val uiResult = vm.uiState.uiResult
    val subreddits = vm.uiState.subreddits

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
            when {
                uiResult is UiResult.Error -> ErrorState(errorMessage = uiResult.errorMessage.orEmpty())
                uiResult is UiResult.Loading -> ThreeDotsLoader()
                uiResult is UiResult.Success && subreddits.isEmpty() -> EmptyState()
                else -> SubredditsList(
                    subreddits = subreddits,
                    onClick = {
                        SearchImagesActivity.launch(
                            context,
                            SearchImagesActivityArguments(
                                subreddit = it.name
                            )
                        )
                    },
                    onSaveChanged = vm.savedSubredditViewModel::onSaveClick
                )
            }
        }
    }
}