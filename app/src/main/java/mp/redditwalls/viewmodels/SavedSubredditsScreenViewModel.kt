package mp.redditwalls.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetSavedSubredditsUseCase
import mp.redditwalls.models.SavedSubredditsScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toSubredditItemUiState

@HiltViewModel
class SavedSubredditsScreenViewModel @Inject constructor(
    private val getSavedSubredditsUseCase: GetSavedSubredditsUseCase,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {
    var savedSubredditsScreenUiState by mutableStateOf( SavedSubredditsScreenUiState())
        private set

    init {
        savedSubredditViewModel.init(viewModelScope)

        subscribeToSavedSubreddits()
        getSavedSubredditsUseCase.init(viewModelScope)
    }

    private fun subscribeToSavedSubreddits() {
        viewModelScope.launch {
            getSavedSubredditsUseCase.sharedFlow.collect {
                savedSubredditsScreenUiState = when (it) {
                    is DomainResult.Error -> savedSubredditsScreenUiState.copy(
                        uiResult = UiResult.Error(it.message)
                    )
                    is DomainResult.Success -> savedSubredditsScreenUiState.copy(
                        subreddits = it.data?.map { subreddit ->
                            subreddit.toSubredditItemUiState()
                        }.orEmpty(),
                        uiResult = UiResult.Success()
                    )
                }
            }
        }
    }
}