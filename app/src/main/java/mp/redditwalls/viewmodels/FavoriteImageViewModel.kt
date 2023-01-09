package mp.redditwalls.viewmodels

import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.ImageId
import mp.redditwalls.domain.usecases.AddFavoriteImageUseCase
import mp.redditwalls.domain.usecases.GetPreferencesUseCase
import mp.redditwalls.domain.usecases.RemoveFavoriteImageUseCase
import mp.redditwalls.local.models.DbImageFolder.Companion.DEFAULT_FOLDER_NAME
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toDomainImage

class FavoriteImageViewModel @Inject constructor(
    private val addFavoriteImageUseCase: AddFavoriteImageUseCase,
    private val removeFavoriteImageUseCase: RemoveFavoriteImageUseCase,
    private val getPreferencesUseCase: GetPreferencesUseCase
) : DelegateViewModel() {
    private fun addFavoriteImage(
        domainImage: DomainImage,
        imageFolderName: String?
    ) {
        coroutineScope.launch {
            val folderName = imageFolderName ?: getPreferencesUseCase(Unit).data?.presetFolderName
            ?: DEFAULT_FOLDER_NAME
            addFavoriteImageUseCase(
                AddFavoriteImageUseCase.Params(
                    image = domainImage,
                    folderName = folderName
                )
            )
        }
    }

    private fun removeFavoriteImage(id: ImageId) {
        coroutineScope.launch {
            removeFavoriteImageUseCase(id)
        }
    }

    fun onLikeClick(
        image: ImageItemUiState,
        isLiked: Boolean,
        folderName: String?
    ) {
        image.isLiked.value = isLiked
        if (isLiked) {
            addFavoriteImage(
                domainImage = image.toDomainImage(),
                imageFolderName = folderName
            )
        } else {
            removeFavoriteImage(image.imageId)
        }
    }
}