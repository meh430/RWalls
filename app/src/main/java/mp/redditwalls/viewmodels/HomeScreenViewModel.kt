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
import mp.redditwalls.domain.usecases.GetPreferencesUseCase
import mp.redditwalls.models.HomeScreenUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemScreenState
import mp.redditwalls.preferences.enums.SortOrder

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    private val favoriteImageViewModelDelegate: FavoriteImageViewModelDelegate,
    private val getPreferencesUseCase: GetPreferencesUseCase
) : FavoriteImageViewModel by favoriteImageViewModelDelegate, ViewModel() {
    val homeScreenUiState = HomeScreenUiState()
    private var hasMoreImages by mutableStateOf(true)

    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        subscribeToHomeFeed()
        subscribeToPreferences()
        getPreferences()
    }

    fun setSortOrder(sortOrder: SortOrder) {
        hasMoreImages = true
        homeScreenUiState.sortOrder.value = sortOrder
        fetchHomeFeed(true)
    }

    fun fetchHomeFeed(reload: Boolean = false) {
        val sortOrder = homeScreenUiState.sortOrder.value
        if (!hasMoreImages || sortOrder == null) {
            return
        }
        homeScreenUiState.uiResult.value = UiResult.Loading()
        if (reload) {
            homeScreenUiState.images.clear()
        }
        viewModelScope.launch {
            getHomeFeedUseCase(
                GetHomeFeedUseCase.Params(
                    sortOrder = sortOrder,
                    reload = reload
                )
            )
        }
    }

    fun setLongPressIndex(index: Int?) {
        homeScreenUiState.longPressedIndex.value = index
    }

    private fun subscribeToHomeFeed() {
        viewModelScope.launch {
            getHomeFeedUseCase.sharedFlow.collect {
                hasMoreImages = it.data?.nextPageId != null
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

    private fun getPreferences() = viewModelScope.launch {
        getPreferencesUseCase(Unit)
    }

    private fun subscribeToPreferences() = viewModelScope.launch {
        getPreferencesUseCase.sharedFlow.collect {
            when (it) {
                is DomainResult.Error -> {}
                is DomainResult.Success -> it.data?.let { preferences ->
                    homeScreenUiState.apply {
                        sortOrder.value = preferences.defaultHomeSort
                        verticalSwipeFeedEnabled.value = preferences.verticalSwipeFeedEnabled
                    }
                    fetchHomeFeed()
                }
            }
        }
    }
}