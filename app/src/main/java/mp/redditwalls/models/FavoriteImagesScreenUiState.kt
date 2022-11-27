package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.local.enums.WallpaperLocation

data class FavoriteImagesScreenUiState(
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf(),
    val selecting: MutableState<Boolean> = mutableStateOf(false),
    val selectedCount: MutableState<Int> = mutableStateOf(0),
    val filter: MutableState<WallpaperLocation> = mutableStateOf(WallpaperLocation.HOME),
    val longPressedIndex: MutableState<Int?> = mutableStateOf(null),
    val showMoveDialog: MutableState<Boolean> = mutableStateOf(false),
    val showDeleteDialog: MutableState<Boolean> = mutableStateOf(false),
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading())
)