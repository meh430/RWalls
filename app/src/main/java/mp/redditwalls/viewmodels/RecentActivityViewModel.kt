package mp.redditwalls.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.usecases.RemoveRecentActivityItemUseCase
import mp.redditwalls.models.RecentActivityItem

class RecentActivityViewModel @Inject constructor(
    private val removeRecentActivityItemUseCase: RemoveRecentActivityItemUseCase
) : DelegateViewModel() {
    private var recentActivityItem: RecentActivityItem? by mutableStateOf(null)

    val showDeleteConfirmationDialog: Boolean
        get() = recentActivityItem != null

    fun askForDeleteConfirmation(recentActivityItem: RecentActivityItem) {
        this.recentActivityItem = recentActivityItem
    }

    fun dismissDeleteConfirmationDialog() {
        recentActivityItem = null
    }

    fun deleteHistory() {
        recentActivityItem?.let {
            coroutineScope.launch {
                removeRecentActivityItemUseCase(listOf(it.dbId))
            }
        }
    }
}