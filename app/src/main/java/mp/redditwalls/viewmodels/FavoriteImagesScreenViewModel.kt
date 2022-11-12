package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.usecases.GetFavoriteImagesUseCase
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.FavoriteImagesScreenUiState
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
        fetchFavoriteImages()
    }

    fun setFilter(wallpaperLocation: WallpaperLocation) {
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