package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.local.models.DbImageFolder.Companion.DEFAULT_FOLDER_NAME

data class FavoriteImagesScreenUiState(
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf(),
    val folderNames: SnapshotStateList<String> = mutableStateListOf(),
    val selecting: MutableState<Boolean> = mutableStateOf(false),
    val selectedCount: MutableState<Int> = mutableStateOf(0),
    val filter: MutableState<String> = mutableStateOf(DEFAULT_FOLDER_NAME),
    val longPressedIndex: MutableState<Int?> = mutableStateOf(null),
    val showMoveDialog: MutableState<Boolean> = mutableStateOf(false),
    val showDeleteDialog: MutableState<Boolean> = mutableStateOf(false),
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading())
)