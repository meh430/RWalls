package mp.redditwalls.viewmodels

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
    val homeScreenUiState = HomeScreenUiState()

    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        fetchHomeFeed(null)
        subscribe()
    }

    fun fetchHomeFeed(sort: SortOrder?) {
        homeScreenUiState.uiResult.value = UiResult.Loading()
        viewModelScope.launch {
            getHomeFeedUseCase(GetHomeFeedUseCase.Params(sortOrder = sort))
        }
    }

    private fun subscribe() {
        viewModelScope.launch {
            getHomeFeedUseCase.sharedFlow.collect {
                homeScreenUiState.apply {
                    when (it) {
                        is DomainResult.Error -> {
                            uiResult.value = UiResult.Error(it.message)
                        }
                        is DomainResult.Success -> {
                            uiResult.value = UiResult.Success()
                            images.addAll(
                                it.data?.images?.map { domainImage ->
                                    domainImage.toImageItemScreenState()
                                }.orEmpty()
                            )
                        }
                    }
                }
            }
        }
    }
}