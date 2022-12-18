package mp.redditwalls.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import mp.redditwalls.activities.SearchImagesActivity
import mp.redditwalls.activities.SearchImagesActivityArguments
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.SearchBar
import mp.redditwalls.design.components.TextRecentActivityCard
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.recentActivityListItems
import mp.redditwalls.ui.components.subredditListItems
import mp.redditwalls.viewmodels.SearchSubredditsScreenViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun SearchSubredditsScreen(vm: SearchSubredditsScreenViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val uiState = vm.uiState
    val uiResult = uiState.uiResult.value

    val searchBarFocusRequester = remember { FocusRequester() }
    Scaffold(
        topBar = {
            SearchBar(
                value = uiState.query.value,
                onValueChanged = { vm.onQueryChanged(it) },
                onSearch = { keyboardController?.hide() },
                leadingIcon = {
                    BackButton(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = { (context as? Activity)?.finish() }
                    )
                },
                trailingIcon = if (uiState.query.value.isEmpty() || !WindowInsets.isImeVisible) {
                    null
                } else {
                    {
                        BackButton(
                            modifier = Modifier.padding(end = 16.dp),
                            cross = true,
                            onClick = { vm.onQueryChanged("") }
                        )
                    }
                },
                focusRequester = searchBarFocusRequester
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumedWindowInsets(it)
                .padding(it),
            contentPadding = PaddingValues(8.dp)
        ) {
            if (uiState.query.value.isNotBlank() && uiState.query.value.length > 2) {
                // search all images
                item {
                    TextRecentActivityCard(
                        icon = Icons.Default.Search,
                        title = getSearchAllString(uiState.query.value),
                        date = "",
                        onClick = {
                            context.startActivity(
                                SearchImagesActivity.getIntent(
                                    context,
                                    SearchImagesActivityArguments(
                                        query = uiState.query.value
                                    )
                                )
                            )
                        }
                    )
                }
            }

            // search history when launched or subreddits when not
            if (uiResult is UiResult.Error) {
                item {
                    ErrorState(errorMessage = uiResult.errorMessage.orEmpty())
                }
            } else if (uiResult is UiResult.Loading) {
                item {
                    ThreeDotsLoader()
                }
            } else if (vm.isSearching()) {
                item {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Subreddits",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                subredditListItems(
                    subreddits = uiState.searchResults,
                    onClick = { subreddit ->
                        context.startActivity(
                            SearchImagesActivity.getIntent(
                                context,
                                SearchImagesActivityArguments(
                                    subreddit = subreddit.name
                                )
                            )
                        )
                    },
                    onSaveChanged = vm.savedSubredditViewModel::onSaveClick
                )
            } else if (uiState.searchHistory.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Search history",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                recentActivityListItems(
                    recentActivityItems = uiState.searchHistory,
                    onClick = {}
                )
            }
        }
    }

    LaunchedEffect(searchBarFocusRequester) {
        vm.fetchSearchHistory()
        delay(200)
        searchBarFocusRequester.requestFocus()
    }
}

private fun getSearchAllString(query: String) = "Search for all images " + if (query.isNotBlank()) {
    "with '$query'"
} else {
    ""
}