package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.repositories.LocalImagesRepository

class AddFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<AddFavoriteImageUseCase.Params, Unit>() {
    override suspend fun execute(params: Params) {
        params.image.run {
            localImagesRepository.insertDbImage(
                DbImage(
                    networkId = networkId,
                    postTitle = postTitle,
                    subredditName = subredditName,
                    postUrl = postUrl,
                    lowQualityUrl = domainImageUrls[params.index].lowQualityUrl,
                    mediumQualityUrl = domainImageUrls[params.index].mediumQualityUrl,
                    sourceUrl = domainImageUrls[params.index].highQualityUrl,
                    refreshLocation = params.refreshLocation.name
                )
            )
        }
    }

    data class Params(
        val image: DomainImage,
        val index: Int = 0,
        val refreshLocation: WallpaperLocation
    )
}