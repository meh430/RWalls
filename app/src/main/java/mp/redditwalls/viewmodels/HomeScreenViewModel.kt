package mp.redditwalls.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.GetHomeFeedUseCase
import mp.redditwalls.models.HomeScreenUiState
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemUiState
import mp.redditwalls.preferences.enums.SortOrder

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel
) : ViewModel() {
    val uiState = HomeScreenUiState()
    val listState = LazyGridState()

    init {
        favoriteImageViewModel.init(viewModelScope)

        subscribeToHomeFeed()
        getHomeFeedUseCase.init(viewModelScope)
    }

    fun setSortOrder(sortOrder: SortOrder) {
        uiState.hasMoreImages.value = true
        uiState.sortOrder.value = sortOrder
        fetchHomeFeed(true)
    }

    fun fetchHomeFeed(reload: Boolean = false) {
        val sortOrder = uiState.sortOrder.value
        if ((!reload && !uiState.hasMoreImages.value) || sortOrder == null) {
            return
        }
        uiState.uiResult.value = UiResult.Loading()
        if (reload) {
            viewModelScope.launch {
                if (uiState.images.isNotEmpty()) {
                    listState.scrollToItem(0)
                }
                uiState.images.clear()
            }
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
                uiState.apply {
                    when (it) {
                        is DomainResult.Error -> {
                            uiResult.value = UiResult.Error(it.message)
                        }
                        is DomainResult.Success -> {
                            uiResult.value = UiResult.Success()
                            images.addAll(
                                it.data?.images?.map { domainImage ->
                                    domainImage.toImageItemUiState()
                                }.orEmpty()
                            )
                            folderNames.clear()
                            folderNames.addAll(it.data?.folderNames.orEmpty())
                            usePresetFolderWhenLiking.value =
                                it.data?.usePresetFolderWhenLiking == true
                            uiState.hasMoreImages.value =
                                it.data?.nextPageId != null && uiState.images.isNotEmpty()
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            getHomeFeedUseCase.shouldReFetchHomeFeed { defaultSortOrder, swipeFeedEnabled ->
                uiState.apply {
                    sortOrder.value = defaultSortOrder
                    verticalSwipeFeedEnabled.value = swipeFeedEnabled
                }
                fetchHomeFeed(reload = true)
            }
        }
    }
}