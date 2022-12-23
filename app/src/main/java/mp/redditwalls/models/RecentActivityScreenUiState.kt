package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.domain.RecentActivityFilter

typealias DailyRecentActivity = Pair<String, List<RecentActivityItem>>

data class RecentActivityScreenUiState(
    val uiResult: MutableState<UiResult> = mutableStateOf(UiResult.Loading()),
    val filter: MutableState<RecentActivityFilter> = mutableStateOf(RecentActivityFilter.ALL),
    val recentActivity: SnapshotStateList<DailyRecentActivity> = mutableStateListOf(),
    val recentActivityMap: MutableMap<String, List<RecentActivityItem>> = mutableMapOf(),
    val showDeleteConfirmation: MutableState<Boolean> = mutableStateOf(false)
)