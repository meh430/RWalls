package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.RecentActivityFilter
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.domain.models.DomainRecentActivityItem.DomainRefreshWallpaperActivityItem
import mp.redditwalls.domain.models.DomainRecentActivityItem.DomainSearchAllActivityItem
import mp.redditwalls.domain.models.DomainRecentActivityItem.DomainSearchSubredditActivityItem
import mp.redditwalls.domain.models.DomainRecentActivityItem.DomainSetWallpaperActivityItem
import mp.redditwalls.domain.models.DomainRecentActivityItem.DomainVisitSubredditActivityItem
import mp.redditwalls.domain.models.toDomainRecentActivityItem
import mp.redditwalls.local.repositories.RecentActivityRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetRecentActivityUseCase @Inject constructor(
    private val recentActivityRepository: RecentActivityRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<List<DomainRecentActivityItem>, RecentActivityFilter>(emptyList()) {
    override fun execute() = combine(
        paramsFlow,
        recentActivityRepository.getDbRecentActivityItems(),
        preferencesRepository.getPreviewResolution()
    ) { params, recentActivities, previewResolution ->
        recentActivities.map {
            it.toDomainRecentActivityItem(previewResolution)
        }.filter {
            when (params) {
                RecentActivityFilter.SEARCH ->
                    it is DomainSearchAllActivityItem || it is DomainSearchSubredditActivityItem
                RecentActivityFilter.IMAGE ->
                    it is DomainRefreshWallpaperActivityItem || it is DomainSetWallpaperActivityItem
                RecentActivityFilter.VISIT -> it is DomainVisitSubredditActivityItem
                RecentActivityFilter.ALL -> true
            }
        }
    }

}