package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetHomeFeedUseCase
import mp.redditwalls.domain.usecases.GetPreferencesUseCase
import mp.redditwalls.models.HomeScreenUiState
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemScreenState
import mp.redditwalls.preferences.enums.SortOrder

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    private val favoriteImageViewModelDelegate: FavoriteImageViewModelDelegate,
    private val getPreferencesUseCase: GetPreferencesUseCase
) : FavoriteImageViewModel by favoriteImageViewModelDelegate, ViewModel() {
    val uiState = HomeScreenUiState()

    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        subscribeToHomeFeed()
        subscribeToPreferences()
        getPreferencesUseCase.init(viewModelScope)
        getHomeFeedUseCase.init(viewModelScope)
        getPreferences()
    }

    fun setSortOrder(sortOrder: SortOrder) {
        uiState.hasMoreImages.value = true
        uiState.sortOrder.value = sortOrder
        fetchHomeFeed(true)
    }

    fun fetchHomeFeed(reload: Boolean = false) {
        val sortOrder = uiState.sortOrder.value
        if (!uiState.hasMoreImages.value || sortOrder == null) {
            return
        }
        uiState.uiResult.value = UiResult.Loading()
        if (reload) {
            uiState.images.clear()
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

    fun setLongPressImage(image: ImageItemUiState?) {
        uiState.longPressedImage.value = image
    }

    private fun subscribeToHomeFeed() {
        viewModelScope.launch {
            getHomeFeedUseCase.sharedFlow.collect {
                uiState.hasMoreImages.value = it.data?.nextPageId != null
                uiState.apply {
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
                    uiState.apply {
                        sortOrder.value = preferences.defaultHomeSort
                        verticalSwipeFeedEnabled.value = preferences.verticalSwipeFeedEnabled
                    }
                    fetchHomeFeed()
                }
            }
        }
    }
}