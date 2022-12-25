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
import mp.redditwalls.models.updateState

@HiltViewModel
class WallpaperScreenViewModel @Inject constructor(
    private val getDetailedImageUseCase: GetDetailedImageUseCase,
    val favoriteImageViewModel: FavoriteImageViewModel,
    val savedSubredditViewModel: SavedSubredditViewModel
) : ViewModel() {
    var uiState: WallpaperScreenUiState by mutableStateOf(WallpaperScreenUiState())

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