package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.preferences.enums.SortOrder

data class HomeScreenUiState(
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf(),
    val longPressedImage: MutableState<ImageItemUiState?> = mutableStateOf(null),
    val sortOrder: MutableState<SortOrder?> = mutableStateOf(null),
    val verticalSwipeFeedEnabled: MutableState<Boolean> = mutableStateOf(false),
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading()),
    val hasMoreImages: MutableState<Boolean> = mutableStateOf(true)
)
