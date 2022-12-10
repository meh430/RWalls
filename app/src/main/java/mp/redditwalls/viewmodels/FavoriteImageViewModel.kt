package mp.redditwalls.viewmodels

import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.usecases.AddFavoriteImageUseCase
import mp.redditwalls.domain.usecases.RemoveFavoriteImageUseCase
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toDomainImage

class FavoriteImageViewModel @Inject constructor(
    private val addFavoriteImageUseCase: AddFavoriteImageUseCase,
    private val removeFavoriteImageUseCase: RemoveFavoriteImageUseCase
) : DelegateViewModel() {
    private fun addFavoriteImage(
        domainImage: DomainImage,
        index: Int,
        refreshLocation: WallpaperLocation
    ) {
        coroutineScope.launch {
            addFavoriteImageUseCase(
                AddFavoriteImageUseCase.Params(
                    image = domainImage,
                    index = index,
                    refreshLocation = refreshLocation
                )
            )
        }
    }

    private fun removeFavoriteImage(id: String) {
        coroutineScope.launch {
            removeFavoriteImageUseCase(id)
        }
    }

    fun onLikeClick(image: ImageItemUiState, isLiked: Boolean, index: Int = 0) {
        image.isLiked.value = isLiked
        if (isLiked) {
            addFavoriteImage(
                domainImage = image.toDomainImage(),
                index = index,
                refreshLocation = WallpaperLocation.BOTH
            )
        } else {
            removeFavoriteImage(image.networkId)
        }
    }
}