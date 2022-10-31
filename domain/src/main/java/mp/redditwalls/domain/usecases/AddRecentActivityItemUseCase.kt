package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.local.enums.RecentActivityType
import mp.redditwalls.local.models.DbRecentActivityItem
import mp.redditwalls.local.repositories.RecentActivityRepository

class AddRecentActivityItemUseCase @Inject constructor(
    private val recentActivityRepository: RecentActivityRepository,
) : UseCase<DomainRecentActivityItem, Unit>() {
    override suspend fun execute(params: DomainRecentActivityItem) {
        when (params) {
            is DomainRecentActivityItem.DomainRefreshWallpaperActivityItem -> DbRecentActivityItem(
                activityType = RecentActivityType.REFRESH_WALLPAPER.name,
                subredditName = params.subredditName,
                lowQualityUrl = params.domainImageUrl.lowQualityUrl,
                mediumQualityUrl = params.domainImageUrl.mediumQualityUrl,
                sourceUrl = params.domainImageUrl.highQualityUrl,
                imageNetworkId = params.imageNetworkId,
                wallpaperLocation = params.wallpaperLocation.name
            )
            is DomainRecentActivityItem.DomainSearchAllActivityItem -> DbRecentActivityItem(
                activityType = RecentActivityType.SEARCH_ALL.name,
                query = params.query
            )
            is DomainRecentActivityItem.DomainSearchSubredditActivityItem -> DbRecentActivityItem(
                activityType = RecentActivityType.SEARCH_SUB.name,
                query = params.query,
                subredditName = params.subredditName
            )
            is DomainRecentActivityItem.DomainSetWallpaperActivityItem -> DbRecentActivityItem(
                activityType = RecentActivityType.SET_WALLPAPER.name,
                subredditName = params.subredditName,
                lowQualityUrl = params.domainImageUrl.lowQualityUrl,
                mediumQualityUrl = params.domainImageUrl.mediumQualityUrl,
                sourceUrl = params.domainImageUrl.highQualityUrl,
                imageNetworkId = params.imageNetworkId,
                wallpaperLocation = params.wallpaperLocation.name
            )
            is DomainRecentActivityItem.DomainVisitSubredditActivityItem -> DbRecentActivityItem(
                activityType = RecentActivityType.VISIT_SUB.name,
                subredditName = params.subredditName
            )
        }.let { recentActivityRepository.insertDbRecentActivityItem(it) }
    }
}