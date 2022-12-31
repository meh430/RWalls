package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.DiscoverResult
import mp.redditwalls.domain.models.RecommendedSubreddit
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.domain.models.toDomainRecentActivityItem
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.getFirstDbImageInAlbum
import mp.redditwalls.local.repositories.LocalImageFoldersRepository
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.local.repositories.RecentActivityRepository
import mp.redditwalls.local.repositories.RecommendedSubredditsRepository
import mp.redditwalls.local.toNetworkIdToDbImageMap
import mp.redditwalls.network.models.TimeFilter
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetDiscoverUseCase @Inject constructor(
    private val recommendedSubredditsRepository: RecommendedSubredditsRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val networkImagesRepository: NetworkImagesRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val localImageFoldersRepository: LocalImageFoldersRepository,
    private val recentActivityRepository: RecentActivityRepository,
    private val preferencesRepository: PreferencesRepository,
) : FlowUseCase<DiscoverResult, Unit>(DiscoverResult()) {
    /**
     * get recommended subreddits that have not been saved
     * fetch subreddit info for the selected subreddits
     * get top images for each subreddit and whether they have been liked or not
     * get most recent activity
     */
    override fun execute() = combine(
        recentActivityRepository.getLimitedDbRecentActivityItems(RECENT_ACTIVITY_LIMIT),
        preferencesRepository.getAllPreferences(),
        localImageFoldersRepository.getDbImageFolderNames()
    ) { recentActivities, preferences, folderNames ->
        val dbSubredditNames = localSubredditsRepository.getDbSubredditsList().map {
            it.name.uppercase()
        }.toSet()
        val dbImageIds = localImagesRepository.getDbImages().toNetworkIdToDbImageMap()
        val recommendations = recommendedSubredditsRepository.getRecommendedSubreddits()
            .filterOutSaved(dbSubredditNames)
            .shuffled()
            .take(RECOMMENDATION_LIMIT).let { subredditNames ->
                networkSubredditsRepository.getSubredditsInfo(subredditNames).subreddits
            }.map { networkSubreddit ->
                networkSubreddit.toDomainSubreddit(
                    isSaved = dbSubredditNames.contains(networkSubreddit.name.uppercase())
                )
            }.map { domainSubreddit ->
                val images = networkImagesRepository.getTopImages(
                    subreddit = domainSubreddit.name,
                    timeFilter = listOf(
                        TimeFilter.YEAR,
                        TimeFilter.ALL,
                        TimeFilter.MONTH
                    ).random(),
                    after = ""
                ).images.map { networkImage ->
                    networkImage.toDomainImage(
                        previewResolution = preferences.previewResolution,
                        dbImage = dbImageIds.getFirstDbImageInAlbum(networkImage.id)
                    )
                }.filter { !it.postTitle.contains("Net Neutrality", true) }
                RecommendedSubreddit(
                    subreddit = domainSubreddit,
                    images = images
                )
            }
        val mostRecentActivities = recentActivities.map {
            it.toDomainRecentActivityItem(preferences.previewResolution)
        }

        DiscoverResult(
            allowNsfw = preferences.allowNsfw,
            recommendations = recommendations,
            mostRecentActivities = mostRecentActivities,
            folderNames = folderNames,
            usePresetFolderWhenLiking = preferences.usePresetFolderWhenLiking
        )
    }

    private fun List<String>.filterOutSaved(saved: Set<String>): List<String> {
        val filtered = filter { rec -> rec !in saved }
        return filtered.ifEmpty {
            // No unsaved recommendations
            this
        }
    }

    companion object {
        private const val RECENT_ACTIVITY_LIMIT = 10
        private const val RECOMMENDATION_LIMIT = 2
    }
}
