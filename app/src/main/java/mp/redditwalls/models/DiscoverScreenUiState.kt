package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class DiscoverScreenUiState(
    val allowNsfw: MutableState<Boolean> = mutableStateOf(false),
    val recommendedSubreddits: SnapshotStateList<RecommendedSubredditUiState> = mutableStateListOf(),
    val recentActivityItems: SnapshotStateList<RecentActivityItem> = mutableStateListOf(),
    val usePresetFolderWhenLiking: MutableState<Boolean> = mutableStateOf(false),
    val folderNames: SnapshotStateList<String> = mutableStateListOf(),
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading())
)

fun DiscoverScreenUiState.clear() {
    recommendedSubreddits.clear()
    recentActivityItems.clear()
    folderNames.clear()
}