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
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toRecentActivityItem
import mp.redditwalls.models.toSubredditItemUiState

@HiltViewModel
class SearchSubredditsScreenViewModel @Inject constructor(
    private val getRecentActivityUseCase: GetRecentActivityUseCase,
    private val searchSubredditsUseCase: SearchSubredditsUseCase,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {
    val searchSubredditsScreenUiState = SearchSubredditsScreenUiState()

    init {
        subscribeToSearchHistory()
        subscribeToSearchResults()
        getRecentActivityUseCase.init(viewModelScope)
        searchSubredditsUseCase.init(viewModelScope)
        savedSubredditViewModel.init(viewModelScope)
    }

    fun fetchSearchHistory() {
        searchSubredditsScreenUiState.uiResult.value = UiResult.Loading()
        viewModelScope.launch {
            getRecentActivityUseCase(RecentActivityFilter.SEARCH)
        }
    }

    fun onQueryChanged(newQuery: String) {
        searchSubredditsScreenUiState.query.value = newQuery
        searchSubreddits()
    }

    fun isSearching() = searchSubredditsScreenUiState.let {
        it.query.value.length > 2 && it.searchResults.isNotEmpty()
    }

    private fun searchSubreddits() {
        viewModelScope.launch {
            searchSubredditsScreenUiState.query.apply {
                if (value.length > 2) {
                    searchSubredditsScreenUiState.uiResult.value = UiResult.Loading()
                    searchSubredditsUseCase(value)
                }
            }
        }
    }

    private fun subscribeToSearchResults() {
        viewModelScope.launch {
            searchSubredditsUseCase.sharedFlow.collect { result ->
                when (result) {
                    is DomainResult.Error -> setErrorState(result.message)
                    is DomainResult.Success -> searchSubredditsScreenUiState.apply {
                        uiResult.value = UiResult.Success()
                        searchResults.clear()
                        searchResults.addAll(
                            result.data?.map { it.toSubredditItemUiState() }.orEmpty()
                        )
                    }
                }
            }
        }
    }

    private fun subscribeToSearchHistory() {
        viewModelScope.launch {
            getRecentActivityUseCase.sharedFlow.collect { result ->
                when (result) {
                    is DomainResult.Error -> setErrorState(result.message)
                    is DomainResult.Success -> searchSubredditsScreenUiState.apply {
                        uiResult.value = UiResult.Success()
                        searchHistory.clear()
                        searchHistory.addAll(
                            result.data?.map { it.toRecentActivityItem() }.orEmpty()
                        )
                    }
                }
            }
        }
    }

    private fun setErrorState(message: String) {
        searchSubredditsScreenUiState.apply {
            uiResult.value = UiResult.Error(message)
            searchHistory.clear()
            searchResults.clear()
        }
    }
}
