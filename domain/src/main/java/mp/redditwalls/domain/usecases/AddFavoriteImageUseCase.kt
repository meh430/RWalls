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
            if (localImagesRepository.dbImageExists(networkId)) {
                return@run
            }

            localImagesRepository.insertDbImage(
                DbImage(
                    networkId = networkId,
                    postTitle = postTitle,
                    subredditName = subredditName,
                    postUrl = postUrl,
                    lowQualityUrl = domainImageUrls[params.index].lowQualityUrl,
                    mediumQualityUrl = domainImageUrls[params.index].mediumQualityUrl,
                    sourceUrl = domainImageUrls[params.index].highQualityUrl,
                    imageFolderName = params.folderName
                )
            )
        }
    }

    data class Params(
        val image: DomainImage,
        val index: Int = 0,
        val folderName: String
    )
}