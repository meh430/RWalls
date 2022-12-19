package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mp.redditwalls.design.components.SelectionState
import mp.redditwalls.domain.usecases.AddFolderUseCase
import mp.redditwalls.domain.usecases.DeleteFolderUseCase
import mp.redditwalls.domain.usecases.GetFavoriteImagesUseCase
import mp.redditwalls.domain.usecases.RemoveFavoriteImagesUseCase
import mp.redditwalls.domain.usecases.UpdateFavoriteImageUseCase
import mp.redditwalls.local.models.DbImageFolder.Companion.DEFAULT_FOLDER_NAME
import mp.redditwalls.models.FavoriteImagesScreenUiState
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.toImageItemItemUiState
import mp.redditwalls.utils.DownloadUtils

@HiltViewModel
class FavoriteImagesScreenViewModel @Inject constructor(
    private val getFavoriteImagesUseCase: GetFavoriteImagesUseCase,
    private val removeFavoriteImagesUseCase: RemoveFavoriteImagesUseCase,
    private val updateFavoriteImageUseCase: UpdateFavoriteImageUseCase,
    private val addFolderUseCase: AddFolderUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel
) : ViewModel() {
    val uiState = FavoriteImagesScreenUiState()

    init {
        favoriteImageViewModel.init(viewModelScope)
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

    fun moveSelectionTo(folderName: String) {
        viewModelScope.launch {
            getSelectedImageIds().let {
                updateFavoriteImageUseCase(
                    UpdateFavoriteImageUseCase.Params(
                        ids = it,
                        folderName = folderName
                    )
                )
                uiState.selectedCount.value -= it.size
            }

            // update selecting state
            uiState.selecting.value =
                uiState.selectedCount.value > 0
            setFilter(folderName)
        }
    }

    fun setFilter(folderName: String = DEFAULT_FOLDER_NAME, force: Boolean = false) {
        if (folderName == uiState.filter.value && !force) {
            return
        }
        stopSelecting()
        uiState.filter.value = folderName
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
                        it.data?.imageFolder?.images?.map { domainImage ->
                            domainImage.toImageItemItemUiState()
                        }.orEmpty()
                    )
                    folderNames.clear()
                    folderNames.addAll(it.data?.folderNames.orEmpty())
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
        }.map {
            it.imageUrl.highQualityUrl.ifEmpty {
                it.imageUrl.mediumQualityUrl
            }.ifEmpty { it.imageUrl.lowQualityUrl }
        }.filter { it.isNotBlank() }
    }
}