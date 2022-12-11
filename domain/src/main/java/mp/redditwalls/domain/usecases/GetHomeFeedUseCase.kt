package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import mp.redditwalls.domain.Utils.toTimeFilter
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.local.Constants.DEFAULT_SUBREDDIT
import mp.redditwalls.local.models.DbSubreddit
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.preferences.PreferencesRepository
import mp.redditwalls.preferences.enums.ImageQuality
import mp.redditwalls.preferences.enums.SortOrder

class GetHomeFeedUseCase @Inject constructor(
    private val networkImagesRepository: NetworkImagesRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val subredditsRepository: LocalSubredditsRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<FeedResult, GetHomeFeedUseCase.Params>(FeedResult()) {

    private lateinit var extras: Extras

    override fun execute() = paramsFlow.map { params ->
        val subreddit = extras.savedSubreddits.joinToString(separator = "+") {
            it.name
        }.ifEmpty {
            DEFAULT_SUBREDDIT
        }
        val dbImageIds =
            localImagesRepository.getDbImages().map { it.networkId }.toSet()
        val after = data.nextPageId?.takeIf { !params.reload }
        val networkImages = when (val sortOrder = params.sortOrder) {
            SortOrder.HOT -> networkImagesRepository.getHotImages(subreddit, after)
            SortOrder.NEW -> networkImagesRepository.getNewImages(subreddit, after)
            else -> networkImagesRepository.getTopImages(
                subreddit, sortOrder.toTimeFilter(), after
            )
        }

        val domainImages = networkImages.images.filter {
            (!extras.allowNsfw && !it.isOver18) || extras.allowNsfw
        }.map {
            it.toDomainImage(
                previewResolution = extras.previewResolution,
                isLiked = dbImageIds.contains(it.id),
            )
        }

        data.copy(
            images = domainImages,
            nextPageId = networkImages.nextPageId
        )
    }

    suspend fun shouldReFetchHomeFeed(onReFetch: (SortOrder, Boolean) -> Unit) {
        combine(
            subredditsRepository.getDbSubreddits(),
            preferencesRepository.getAllowNsfw(),
            preferencesRepository.getPreviewResolution(),
            preferencesRepository.getDefaultHomeSort(),
            preferencesRepository.getVerticalSwipeFeedEnabled()
        ) { savedSubreddits, allowNsfw, previewResolution, defaultSort, swipeEnabled ->
            extras = Extras(
                savedSubreddits = savedSubreddits,
                allowNsfw = allowNsfw,
                previewResolution = previewResolution
            )
            onReFetch(defaultSort, swipeEnabled)
        }.collect()
    }

    data class Params(
        val sortOrder: SortOrder,
        val reload: Boolean = false
    )

    private data class Extras(
        val savedSubreddits: List<DbSubreddit>,
        val allowNsfw: Boolean,
        val previewResolution: ImageQuality
    )
}