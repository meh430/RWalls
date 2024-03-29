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
import mp.redditwalls.domain.usecases.GetDetailedImageUseCase
import mp.redditwalls.models.UiResult
import mp.redditwalls.models.WallpaperScreenUiState
import mp.redditwalls.models.hideUi
import mp.redditwalls.models.showUi
import mp.redditwalls.models.updateState

@HiltViewModel
class WallpaperScreenViewModel @Inject constructor(
    private val getDetailedImageUseCase: GetDetailedImageUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {
    var uiState: WallpaperScreenUiState by mutableStateOf(WallpaperScreenUiState())
    var showSetWallpaperDialog by mutableStateOf(false)
        private set
    var showFolderSelectDialog by mutableStateOf(false)
        private set

    init {
        subscribeToImage()
        getDetailedImageUseCase.init(viewModelScope)

        favoriteImageViewModel.init(viewModelScope)
        savedSubredditViewModel.init(viewModelScope)
    }

    fun fetchWallpaper(id: String) {
        uiState = uiState.copy(uiResult = UiResult.Loading())
        viewModelScope.launch {
            getDetailedImageUseCase(id)
        }
    }

    fun showSetWallpaperDialog() {
        showSetWallpaperDialog = true
    }

    fun dismissSetWallpaperDialog() {
        showSetWallpaperDialog = false
    }

    fun showFolderSelectDialog() {
        showFolderSelectDialog = true
    }

    fun dismissFolderSelectDialog() {
        showFolderSelectDialog = false
    }

    fun toggleUiVisibility() {
        if (uiState.shouldHideUi) {
            showUi()
        } else {
            hideUi()
        }
    }

    fun showUi() {
        uiState = uiState.showUi()
    }

    private fun hideUi() {
        uiState = uiState.hideUi()
    }

    private fun subscribeToImage() {
        viewModelScope.launch {
            getDetailedImageUseCase.sharedFlow.collect {
                when (it) {
                    is DomainResult.Error -> uiState = uiState.copy(
                        uiResult = UiResult.Error(it.message)
                    )
                    is DomainResult.Success -> it.data?.let { data ->
                        uiState = uiState.updateState(data)
                    }
                }
            }
        }
    }
}