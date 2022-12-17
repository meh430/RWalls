package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.RecentActivityFilter
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetRecentActivityUseCase
import mp.redditwalls.domain.usecases.SearchSubredditsUseCase
import mp.redditwalls.models.SearchSubredditsScreenUiState

@HiltViewModel
class SearchSubredditsScreenViewModel @Inject constructor(
    private val getRecentActivityUseCase: GetRecentActivityUseCase,
    private val searchSubredditsUseCase: SearchSubredditsUseCase
) : ViewModel() {
    val searchSubredditsScreenUiState = SearchSubredditsScreenUiState()

    init {
        subscribeToSearchHistory()
        subscribeToSearchResults()
        getRecentActivityUseCase.init(viewModelScope)
        searchSubredditsUseCase.init(viewModelScope)
    }

    fun fetchSearchHistory() {
        viewModelScope.launch {
            getRecentActivityUseCase(RecentActivityFilter.SEARCH)
        }
    }

    fun onQueryChanged(newQuery: String) {
        searchSubredditsScreenUiState.query.value = newQuery
    }

    private fun searchSubreddits() {
        viewModelScope.launch {
            searchSubredditsScreenUiState.query.apply {
                if (value.length > 2) {
                    searchSubredditsUseCase(value)
                }
            }
        }
    }

    private fun subscribeToSearchResults() {
        viewModelScope.launch {
            searchSubredditsUseCase.sharedFlow.collect {
                when (it) {
                    is DomainResult.Error -> TODO()
                    is DomainResult.Success -> TODO()
                }
            }
        }
    }

    private fun subscribeToSearchHistory() {
        viewModelScope.launch {
            getRecentActivityUseCase.sharedFlow.collect {
                when (it) {
                    is DomainResult.Error -> TODO()
                    is DomainResult.Success -> TODO()
                }
            }
        }
    }
}
