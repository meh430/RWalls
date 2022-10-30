package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.domain.models.DiscoverResult
import mp.redditwalls.domain.models.RecommendedSubreddit
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.domain.models.toDomainRecentActivityItem
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.local.repositories.RecentActivityRepository
import mp.redditwalls.local.repositories.RecommendedSubredditsRepository
import mp.redditwalls.network.models.TimeFilter
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetDiscoverUseCase @Inject constructor(
    private val recommendedSubredditsRepository: RecommendedSubredditsRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val networkImagesRepository: NetworkImagesRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val recentActivityRepository: RecentActivityRepository,
    private val preferencesRepository: PreferencesRepository,
) : FlowUseCase<DiscoverResult, Unit>(DiscoverResult()) {
    override suspend operator fun invoke(params: Unit) = withContext(Dispatchers.IO) {
        /**
         * get recommended subreddits that have not been saved
         * fetch subreddit info for the selected subreddits
         * get top images for each subreddit and whether they have been liked or not
         * get most recent activity
         */
        combine(
            localSubredditsRepository.getDbSubreddits(),
            localImagesRepository.getDbImagesFlow(),
            recentActivityRepository.getLimitedDbRecentActivityItems(RECENT_ACTIVITY_LIMIT),
            preferencesRepository.getAllowNsfw(),
            preferencesRepository.getPreviewResolution()
        ) { dbSubreddits, dbImages, recentActivities, allowNsfw, previewResolution ->
            val subredditNameToDbId = dbSubreddits.associate { it.name to it.id }
            val imageNetworkIdToDbId = dbImages.associate { it.networkId to it.id }
            val recommendations =
                recommendedSubredditsRepository.getRecommendedSubreddits().filter {
                    !subredditNameToDbId.containsKey(it)
                }.let { subredditNames ->
                    networkSubredditsRepository.getSubredditsInfo(subredditNames).subreddits
                }.map { networkSubreddit ->
                    networkSubreddit.toDomainSubreddit(
                        isSaved = subredditNameToDbId.containsKey(networkSubreddit.name),
                        dbId = subredditNameToDbId[networkSubreddit.name] ?: -1
                    )
                }.map { domainSubreddit ->
                    val images = networkImagesRepository.getTopImages(
                        subreddit = domainSubreddit.name,
                        timeFilter = TimeFilter.ALL,
                        after = ""
                    ).images.map { networkImage ->
                        val galleryItem = networkImage.galleryItems.first()
                        networkImage.toDomainImage(
                            imageUrl = previewResolution.getImageUrl(
                                galleryItem.lowQualityUrl,
                                galleryItem.mediumQualityUrl,
                                galleryItem.sourceUrl
                            ),
                            isLiked = imageNetworkIdToDbId.containsKey(networkImage.id),
                            dbId = imageNetworkIdToDbId[networkImage.id] ?: -1
                        )
                    }
                    RecommendedSubreddit(
                        subreddit = domainSubreddit,
                        images = images
                    )
                }
            val mostRecentActivities = recentActivities.map {
                it.toDomainRecentActivityItem(previewResolution)
            }

            DiscoverResult(
                allowNsfw = allowNsfw,
                recommendations = recommendations,
                mostRecentActivities = mostRecentActivities
            )
        }.resolveResult()
    }

    companion object {
        private const val RECENT_ACTIVITY_LIMIT = 10
    }
}
