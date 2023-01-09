package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.repositories.LocalImagesRepository

class AddFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<AddFavoriteImageUseCase.Params, Unit>() {
    override suspend fun execute(params: Params) {
        params.image.run {
            if (localImagesRepository.dbImageExists(imageId.dbImageId)) {
                localImagesRepository.updateDbImageFolder(
                    id = imageId.dbImageId,
                    folderName = params.folderName
                )
                return@run
            }

            val imageUrl = imageUrls.first()
            localImagesRepository.insertDbImage(
                DbImage(
                    id = imageId.dbImageId,
                    postTitle = postTitle,
                    subredditName = subredditName,
                    postUrl = postUrl,
                    lowQualityUrl = imageUrl.lowQualityUrl,
                    mediumQualityUrl = imageUrl.mediumQualityUrl,
                    sourceUrl = imageUrl.highQualityUrl,
                    imageFolderName = params.folderName
                )
            )
        }
    }

    data class Params(
        val image: DomainImage,
        val folderName: String
    )
}