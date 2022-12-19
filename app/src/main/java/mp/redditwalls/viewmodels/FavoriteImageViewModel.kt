package mp.redditwalls.viewmodels

import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.usecases.AddFavoriteImageUseCase
import mp.redditwalls.domain.usecases.RemoveFavoriteImageUseCase
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toDomainImage

class FavoriteImageViewModel @Inject constructor(
    private val addFavoriteImageUseCase: AddFavoriteImageUseCase,
    private val removeFavoriteImageUseCase: RemoveFavoriteImageUseCase
) : DelegateViewModel() {
    private fun addFavoriteImage(
        domainImage: DomainImage,
        index: Int,
        imageFolderName: String
    ) {
        coroutineScope.launch {
            addFavoriteImageUseCase(
                AddFavoriteImageUseCase.Params(
                    image = domainImage,
                    index = index,
                    folderName = imageFolderName
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
                imageFolderName = "default"
            )
        } else {
            removeFavoriteImage(image.networkId)
        }
    }
}