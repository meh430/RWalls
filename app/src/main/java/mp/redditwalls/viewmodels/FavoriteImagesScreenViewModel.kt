package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.design.components.SelectionState
import mp.redditwalls.domain.usecases.GetFavoriteImagesUseCase
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.FavoriteImagesScreenUiState
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemScreenState

@HiltViewModel
class FavoriteImagesScreenViewModel @Inject constructor(
    private val getFavoriteImagesUseCase: GetFavoriteImagesUseCase,
    private val favoriteImageViewModelDelegate: FavoriteImageViewModelDelegate,
) : FavoriteImageViewModel by favoriteImageViewModelDelegate, ViewModel() {
    val favoriteImagesScreenUiState = FavoriteImagesScreenUiState()

    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        subscribeToFavoriteImages()
        getFavoriteImagesUseCase.init(viewModelScope)
    }

    private fun startSelecting() {
        if (favoriteImagesScreenUiState.selecting.value) {
            return
        }
        favoriteImagesScreenUiState.apply {
            selectedCount.value = 0
            selecting.value = true
            images.forEach {
                it.selectionState.value = SelectionState.SELECTABLE
            }
        }
    }

    fun stopSelecting() {
        if (!favoriteImagesScreenUiState.selecting.value) {
            return
        }
        favoriteImagesScreenUiState.apply {
            selectedCount.value = 0
            selecting.value = false
            images.forEach {
                it.selectionState.value = SelectionState.NOT_SELECTABLE
            }
        }
    }

    fun selectImage(image: ImageItemUiState) {
        when (image.selectionState.value) {
            SelectionState.SELECTED -> {
                favoriteImagesScreenUiState.selectedCount.value -= 1
                image.selectionState.value = SelectionState.SELECTABLE
                if (favoriteImagesScreenUiState.selectedCount.value == 0) {
                    stopSelecting()
                }
            }
            SelectionState.SELECTABLE -> {
                favoriteImagesScreenUiState.selectedCount.value += 1
                image.selectionState.value = SelectionState.SELECTED
            }
            SelectionState.NOT_SELECTABLE -> {
                startSelecting()
                selectImage(image)
            }
        }
    }

    fun selectAll() {
        favoriteImagesScreenUiState.images.forEach {
            selectImage(it)
        }
        favoriteImagesScreenUiState.selectedCount.value = favoriteImagesScreenUiState.images.size
    }

    fun setFilter(wallpaperLocation: WallpaperLocation) {
        favoriteImagesScreenUiState.selecting.value = false
        favoriteImagesScreenUiState.filter.value = wallpaperLocation
        fetchFavoriteImages()
    }

    private fun fetchFavoriteImages() {
        favoriteImagesScreenUiState.uiResult.value = UiResult.Loading()
        viewModelScope.launch {
            getFavoriteImagesUseCase(
                GetFavoriteImagesUseCase.Params(favoriteImagesScreenUiState.filter.value)
            )
        }
    }

    private fun subscribeToFavoriteImages() {
        viewModelScope.launch {
            getFavoriteImagesUseCase.sharedFlow.collect {
                favoriteImagesScreenUiState.apply {
                    uiResult.value = UiResult.Success()
                    images.clear()
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