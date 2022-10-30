package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.domain.Utils.toTimeFilter
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.preferences.PreferencesRepository
import mp.redditwalls.preferences.enums.SortOrder

class GetHomeFeedUseCase @Inject constructor(
    private val networkImagesRepository: NetworkImagesRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val subredditsRepository: LocalSubredditsRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<FeedResult, GetHomeFeedUseCase.Params>(FeedResult()) {

    override suspend operator fun invoke(params: Params) = withContext(Dispatchers.IO) {
        combine(
            subredditsRepository.getDbSubreddits(),
            localImagesRepository.getDbImagesFlow(),
            preferencesRepository.getAllowNsfw(),
            preferencesRepository.getPreviewResolution(),
            preferencesRepository.getDefaultHomeSort()
        ) { savedSubreddits, dbImages, allowNsfw, previewResolution, defaultSortOrder ->
            val subreddit = savedSubreddits.joinToString(separator = "+") { it.name }
            val dbImagesNetworkIds = dbImages.associate { it.networkId to it.id }
            val after = data.nextPageId
            val networkImages = when (val sortOrder = params.sortOrder ?: defaultSortOrder) {
                SortOrder.HOT -> networkImagesRepository.getHotImages(subreddit, after)
                SortOrder.NEW -> networkImagesRepository.getNewImages(subreddit, after)
                else -> networkImagesRepository.getTopImages(
                    subreddit, sortOrder.toTimeFilter(), after
                )
            }

            val domainImages = networkImages.images.filter {
                // filter sfw images when not allowing nsfw
                !allowNsfw && !it.isOver18
            }.map {
                val galleryItem = it.galleryItems.first()
                it.toDomainImage(
                    imageUrl = previewResolution.getImageUrl(
                        galleryItem.lowQualityUrl,
                        galleryItem.mediumQualityUrl,
                        galleryItem.sourceUrl
                    ),
                    isLiked = dbImagesNetworkIds.contains(it.id),
                    dbId = dbImagesNetworkIds[it.id] ?: -1
                )
            }

            data.copy(
                images = data.images + domainImages,
                nextPageId = networkImages.nextPageId
            )
        }.resolveResult()
    }

    data class Params(
        val sortOrder: SortOrder? = null
    )
}