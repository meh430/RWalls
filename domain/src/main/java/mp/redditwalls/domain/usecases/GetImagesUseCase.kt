package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.Utils.toTimeFilter
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.network.models.NetworkImages
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.PreferencesRepository
import mp.redditwalls.preferences.enums.SortOrder

class GetImagesUseCase @Inject constructor(
    private val networkImagesRepository: NetworkImagesRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<FeedResult, GetImagesUseCase.Params>(FeedResult()) {
    override fun execute(params: Params) = combine(
        localImagesRepository.getDbImagesFlow(),
        localSubredditsRepository.getDbSubreddits(),
        preferencesRepository.getPreviewResolution()
    ) { dbImages, dbSubreddits, previewResolution ->
        val subredditNameToDbId = dbSubreddits.associate { it.name to it.id }
        val imageNetworkIdToDbId = dbImages.associate { it.networkId to it.id }

        val networkImages: NetworkImages =
            if (params.subreddit != null && params.query == null) {
                // view subreddit results
                when (params.sortOrder) {
                    SortOrder.HOT -> networkImagesRepository.getHotImages(
                        subreddit = params.subreddit,
                        after = data.nextPageId
                    )
                    SortOrder.NEW -> networkImagesRepository.getNewImages(
                        subreddit = params.subreddit,
                        after = data.nextPageId
                    )
                    else -> networkImagesRepository.getTopImages(
                        subreddit = params.subreddit,
                        timeFilter = params.sortOrder.toTimeFilter(),
                        after = data.nextPageId
                    )
                }
            } else if (params.subreddit == null && params.query != null) {
                // search all
                networkImagesRepository.searchAllImages(
                    query = params.query,
                    sort = getSort(params.sortOrder),
                    time = params.sortOrder.toTimeFilter(),
                    after = data.nextPageId
                )
            } else if (params.subreddit != null && params.query != null) {
                // search subreddit
                networkImagesRepository.searchImagesInSubreddit(
                    subreddit = params.subreddit,
                    query = params.query,
                    sort = getSort(params.sortOrder),
                    time = params.sortOrder.toTimeFilter(),
                    after = data.nextPageId
                )
            } else {
                NetworkImages(nextPageId = "")
            }

        val domainImages = networkImages.images.map {
            it.toDomainImage(
                previewResolution = previewResolution,
                isLiked = imageNetworkIdToDbId.containsKey(it.id),
                dbId = imageNetworkIdToDbId[it.id] ?: -1
            )
        }

        val domainSubreddit = params.subreddit?.takeIf { data.subreddit == null }?.let {
            networkSubredditsRepository.getSubredditDetail(it).let { networkSubreddit ->
                networkSubreddit.toDomainSubreddit(
                    isSaved = subredditNameToDbId.containsKey(networkSubreddit.name),
                    dbId = subredditNameToDbId[networkSubreddit.name] ?: -1
                )
            }
        } ?: data.subreddit

        FeedResult(
            images = data.images + domainImages,
            subreddit = domainSubreddit,
            nextPageId = networkImages.nextPageId
        )
    }

    private fun getSort(sortOrder: SortOrder) = when (sortOrder) {
        SortOrder.NEW, SortOrder.HOT -> sortOrder.name.lowercase()
        else -> "top"
    }

    data class Params(
        val subreddit: String? = null,
        val query: String? = null,
        val sortOrder: SortOrder = SortOrder.HOT
    )
}