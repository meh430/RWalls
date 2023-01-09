package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class SearchSubredditsScreenUiState(
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading()),
    val query: MutableState<String> = mutableStateOf(""),
    val searchHistory: SnapshotStateList<RecentActivityItem> = mutableStateListOf(),
    val searchResults: SnapshotStateList<SubredditItemUiState> = mutableStateListOf()
)