package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.preferences.enums.SortOrder

data class SearchImagesScreenUiState(
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading()),
    val subredditItemUiState: MutableState<SubredditItemUiState?> = mutableStateOf(null),
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf(),
    val hasMoreImages: MutableState<Boolean> = mutableStateOf(false),
    val query: MutableState<String> = mutableStateOf(""),
    val sortOrder: MutableState<SortOrder> = mutableStateOf(SortOrder.HOT),
    val subredditName: MutableState<String?> = mutableStateOf(""),
    val usePresetFolderWhenLiking: MutableState<Boolean> = mutableStateOf(false),
    val folderNames: SnapshotStateList<String> = mutableStateListOf(),
    val longPressedImage: MutableState<ImageItemUiState?> = mutableStateOf(null)
)