package mp.redditwalls.domain.usecases

import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.domain.models.DiscoverResult
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.models.RecommendedSubreddit
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.enums.RecentActivityType
import mp.redditwalls.local.enums.WallpaperLocation
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
                when (RecentActivityType.valueOf(it.activityType)) {
                    RecentActivityType.SEARCH_SUB -> DomainRecentActivityItem.DomainSearchSubredditActivityItem(
                        dbId = it.id,
                        createdAt = Date(it.createdAt),
                        subredditName = it.subredditName,
                        query = it.query
                    )
                    RecentActivityType.SEARCH_ALL -> DomainRecentActivityItem.DomainSearchAllActivityItem(
                        dbId = it.id,
                        createdAt = Date(it.createdAt),
                        query = it.query
                    )
                    RecentActivityType.VISIT_SUB -> DomainRecentActivityItem.DomainVisitSubredditActivityItem(
                        dbId = it.id,
                        createdAt = Date(it.createdAt),
                        subredditName = it.subredditName
                    )
                    RecentActivityType.SET_WALLPAPER -> DomainRecentActivityItem.DomainSetWallpaperActivityItem(
                        dbId = it.id,
                        createdAt = Date(it.createdAt),
                        subredditName = it.subredditName,
                        imageUrl = previewResolution.getImageUrl(
                            it.lowQualityUrl,
                            it.mediumQualityUrl,
                            it.sourceUrl
                        ),
                        imageNetworkId = it.networkId,
                        wallpaperLocation = WallpaperLocation.valueOf(it.wallpaperLocation)
                    )
                    RecentActivityType.REFRESH_WALLPAPER -> DomainRecentActivityItem.DomainRefreshWallpaperActivityItem(
                        dbId = it.id,
                        createdAt = Date(it.createdAt),
                        subredditName = it.subredditName,
                        imageUrl = previewResolution.getImageUrl(
                            it.lowQualityUrl,
                            it.mediumQualityUrl,
                            it.sourceUrl
                        ),
                        imageNetworkId = it.networkId,
                        wallpaperLocation = WallpaperLocation.valueOf(it.wallpaperLocation)
                    )
                }
            }

            DiscoverResult(
                allowNsfw = allowNsfw,
                recommendations = recommendations,
                mostRecentActivities = mostRecentActivities
            )
        }.catch { e ->
            updateData(DomainResult.Error(message = e.localizedMessage.orEmpty()))
        }.flowOn(Dispatchers.IO).collect {
            updateData(DomainResult.Success(it))
        }
    }

    companion object {
        private const val RECENT_ACTIVITY_LIMIT = 10
    }
}
