package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.RecentActivityFilter
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.models.RecentActivityResult
import mp.redditwalls.domain.usecases.GetRecentActivityUseCase
import mp.redditwalls.domain.usecases.RemoveRecentActivityItemUseCase
import mp.redditwalls.models.RecentActivityScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toRecentActivityItem

@HiltViewModel
class RecentActivityScreenViewModel @Inject constructor(
    private val getRecentActivityUseCase: GetRecentActivityUseCase,
    private val removeRecentActivityItemUseCase: RemoveRecentActivityItemUseCase,
    val recentActivityViewModel: RecentActivityViewModel
) : ViewModel() {
    val uiState = RecentActivityScreenUiState()

    init {
        recentActivityViewModel.init(viewModelScope)

        subscribeToRecentActivity()
        getRecentActivityUseCase.init(viewModelScope)
    }

    fun fetchRecentActivity(filter: RecentActivityFilter) {
        uiState.apply {
            this.filter.value = filter
            uiResult.value = UiResult.Loading()
        }
        viewModelScope.launch {
            getRecentActivityUseCase(filter)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            removeRecentActivityItemUseCase(emptyList())
        }
    }

    fun deleteHistoryForDate(date: String) {
        viewModelScope.launch {
            uiState.recentActivityMap[date]?.map {
                it.dbId
            }?.takeIf {
                it.isNotEmpty()
            }?.let {
                removeRecentActivityItemUseCase(it)
            }
        }
    }

    private fun subscribeToRecentActivity() {
        viewModelScope.launch {
            getRecentActivityUseCase.sharedFlow.collect {
                uiState.apply {
                    when (it) {
                        is DomainResult.Error -> uiResult.value = UiResult.Error(it.message)
                        is DomainResult.Success -> {
                            uiResult.value = UiResult.Success()
                            it.data?.let(::updateState)
                        }
                    }
                }
            }
        }
    }

    private fun updateState(recentActivityResult: RecentActivityResult) = uiState.apply {
        recentActivity.clear()
        recentActivity.addAll(
            recentActivityResult.recentActivityGroupedByDay.map { (date, domainRecentActivity) ->
                date to domainRecentActivity.map { it.toRecentActivityItem() }
            }
        )
        recentActivityMap.clear()
        recentActivityMap.putAll(
            recentActivityResult.recentActivityGroupedByDay.map { (date, domainRecentActivity) ->
                date to domainRecentActivity.map { it.toRecentActivityItem() }
            }
        )
    }
}
