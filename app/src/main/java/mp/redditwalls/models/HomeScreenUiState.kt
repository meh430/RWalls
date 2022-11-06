package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.preferences.enums.SortOrder

data class HomeScreenUiState(
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf(),
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading()),
    val sortOrder: MutableState<SortOrder?> = mutableStateOf(null)
)
