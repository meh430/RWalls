package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.DetailedImageResult
import mp.redditwalls.domain.models.DomainImageUrl
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.network.repositories.ImgurRepository
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.enums.ImageQuality

class GetDetailedImageUseCase @Inject constructor(
    private val networkImagesRepository: NetworkImagesRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val imgurRepository: ImgurRepository
) : FlowUseCase<DetailedImageResult, String>(DetailedImageResult()) {
    // takes network image id
    override fun execute() = combine(
        paramsFlow,
        localSubredditsRepository.getDbSubreddits()
    ) { params, dbSubreddits ->
        val dbSubredditNames = dbSubreddits.map { it.name }.toSet()
        val dbImageIds = localImagesRepository.getDbImages().map { it.networkId }.toSet()

        val networkImage = networkImagesRepository.getImage(params)

        val domainSubreddit = networkSubredditsRepository.getSubredditDetail(
            networkImage.subredditName
        ).toDomainSubreddit(
            isSaved = dbSubredditNames.contains(networkImage.subredditName)
        )

        val domainImage = if (networkImage.imgurAlbumId.isNotEmpty()) {
            // fetch all images from imgur
            networkImage.toDomainImage(
                previewResolution = ImageQuality.HIGH,
                isLiked = dbImageIds.contains(networkImage.id),
            ).copy(
                domainImageUrls = imgurRepository.getAlbum(networkImage.imgurAlbumId).images.map {
                    DomainImageUrl(
                        url = it.link,
                        lowQualityUrl = it.link,
                        mediumQualityUrl = it.link,
                        highQualityUrl = it.link
                    )
                }
            )
        } else {
            networkImage.toDomainImage(
                previewResolution = ImageQuality.HIGH,
                isLiked = dbImageIds.contains(networkImage.id),
            )
        }

        DetailedImageResult(
            domainImage = domainImage,
            domainSubreddit = domainSubreddit
        )
    }
}