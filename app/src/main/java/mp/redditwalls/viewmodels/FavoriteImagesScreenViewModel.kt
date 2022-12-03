package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mp.redditwalls.design.components.SelectionState
import mp.redditwalls.domain.usecases.GetFavoriteImagesUseCase
import mp.redditwalls.domain.usecases.RemoveFavoriteImagesUseCase
import mp.redditwalls.domain.usecases.UpdateFavoriteImageUseCase
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.FavoriteImagesScreenUiState
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemItemUiState
import mp.redditwalls.utils.DownloadUtils

@HiltViewModel
class FavoriteImagesScreenViewModel @Inject constructor(
    private val getFavoriteImagesUseCase: GetFavoriteImagesUseCase,
    private val favoriteImageViewModelDelegate: FavoriteImageViewModelDelegate,
    private val removeFavoriteImagesUseCase: RemoveFavoriteImagesUseCase,
    private val updateFavoriteImageUseCase: UpdateFavoriteImageUseCase
) : FavoriteImageViewModel by favoriteImageViewModelDelegate, ViewModel() {
    val uiState = FavoriteImagesScreenUiState()

    init {
        favoriteImageViewModelDelegate.coroutineScope = viewModelScope
        subscribeToFavoriteImages()
        getFavoriteImagesUseCase.init(viewModelScope)
    }

    fun startSelecting(image: ImageItemUiState) {
        if (uiState.selecting.value) {
            return
        }
        uiState.apply {
            selectedCount.value = 1
            selecting.value = true
            images.forEach {
                it.selectionState.value = if (image.networkId == it.networkId) {
                    SelectionState.SELECTED
                } else {
                    SelectionState.SELECTABLE
                }
            }
        }
    }

    fun stopSelecting() {
        if (!uiState.selecting.value) {
            return
        }
        uiState.apply {
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
                uiState.selectedCount.value -= 1
                image.selectionState.value = SelectionState.SELECTABLE
                if (uiState.selectedCount.value == 0) {
                    stopSelecting()
                }
            }
            SelectionState.SELECTABLE -> {
                uiState.selectedCount.value += 1
                image.selectionState.value = SelectionState.SELECTED
            }
            else -> {}
        }
    }

    fun selectAll() {
        if (!uiState.selecting.value) {
            return
        }
        uiState.images.forEach {
            it.selectionState.value = SelectionState.SELECTED
        }
        uiState.selectedCount.value = uiState.images.size
    }

    fun deleteSelection() {
        viewModelScope.launch {
            getSelectedImageIds().let {
                removeFavoriteImagesUseCase(it)
                uiState.selectedCount.value -= it.size
            }

            // update selecting state
            uiState.selecting.value =
                uiState.selectedCount.value > 0
        }
    }

    fun downloadSelection(downloadUtils: DownloadUtils) {
        viewModelScope.launch {
            downloadUtils.downloadImages(getSelectedImageUrls())
        }
    }

    fun moveSelectionTo(wallpaperLocation: WallpaperLocation) {
        viewModelScope.launch {
            getSelectedImageIds().let {
                updateFavoriteImageUseCase(
                    UpdateFavoriteImageUseCase.Params(
                        ids = it,
                        refreshLocation = wallpaperLocation
                    )
                )
                uiState.selectedCount.value -= it.size
            }

            // update selecting state
            uiState.selecting.value =
                uiState.selectedCount.value > 0
            setFilter(wallpaperLocation)
        }
    }

    fun setFilter(wallpaperLocation: WallpaperLocation, force: Boolean = false) {
        if (wallpaperLocation == uiState.filter.value && !force) {
            return
        }
        stopSelecting()
        uiState.filter.value = wallpaperLocation
        fetchFavoriteImages()
    }

    private fun fetchFavoriteImages() {
        uiState.uiResult.value = UiResult.Loading()
        viewModelScope.launch {
            getFavoriteImagesUseCase(
                GetFavoriteImagesUseCase.Params(uiState.filter.value)
            )
        }
    }

    private fun subscribeToFavoriteImages() {
        viewModelScope.launch {
            getFavoriteImagesUseCase.sharedFlow.collect {
                uiState.apply {
                    uiResult.value = UiResult.Success()
                    images.clear()
                    images.addAll(
                        it.data?.images?.map { domainImage ->
                            domainImage.toImageItemItemUiState()
                        }.orEmpty()
                    )
                }
            }
        }
    }

    private suspend fun getSelectedImageIds() = withContext(Dispatchers.IO) {
        uiState.images.filter {
            it.selectionState.value == SelectionState.SELECTED
        }.map { it.networkId }
    }

    private suspend fun getSelectedImageUrls() = withContext(Dispatchers.IO) {
        uiState.images.filter {
            it.selectionState.value == SelectionState.SELECTED
        }.map { it.imageUrl.highQualityUrl }
    }
}