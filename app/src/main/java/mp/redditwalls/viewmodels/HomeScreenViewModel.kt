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
import mp.redditwalls.domain.usecases.GetHomeFeedUseCase
import mp.redditwalls.models.HomeScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemScreenState
import mp.redditwalls.preferences.enums.SortOrder

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    private val favoriteImageViewModelDelegate: FavoriteImageViewModelDelegate
) : FavoriteImageViewModel by favoriteImageViewModelDelegate, ViewModel() {
    var homeScreenUiState by mutableStateOf(HomeScreenUiState())
        private set

    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        fetchHomeFeed(null)
        subscribe()
    }

    fun fetchHomeFeed(sort: SortOrder?) {
        viewModelScope.launch {
            getHomeFeedUseCase(GetHomeFeedUseCase.Params(sortOrder = sort))
        }
    }

    private fun subscribe() {
        homeScreenUiState = homeScreenUiState.copy(uiResult = UiResult.Loading())
        viewModelScope.launch {
            getHomeFeedUseCase.sharedFlow.collect {
                homeScreenUiState = when (it) {
                    is DomainResult.Error -> {
                        homeScreenUiState.copy(
                            uiResult = UiResult.Error(it.message)
                        )
                    }
                    is DomainResult.Success -> {
                        homeScreenUiState.copy(
                            uiResult = UiResult.Success(),
                            images = it.data?.images?.map { domainImage ->
                                domainImage.toImageItemScreenState()
                            } ?: emptyList()
                        )
                    }
                }
            }
        }
    }
}