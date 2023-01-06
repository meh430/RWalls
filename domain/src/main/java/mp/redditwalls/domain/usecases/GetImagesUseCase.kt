package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.Utils.toTimeFilter
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.getFirstDbImageInAlbum
import mp.redditwalls.local.repositories.LocalImageFoldersRepository
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.local.toNetworkIdToDbImageMap
import mp.redditwalls.network.models.NetworkImages
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.PreferencesRepository
import mp.redditwalls.preferences.enums.SortOrder

class GetImagesUseCase @Inject constructor(
    private val networkImagesRepository: NetworkImagesRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val localImageFoldersRepository: LocalImageFoldersRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<FeedResult, GetImagesUseCase.Params>(FeedResult()) {
    override fun execute() = combine(
        paramsFlow,
        preferencesRepository.getAllPreferences(),
        localImageFoldersRepository.getDbImageFolderNames()
    ) { params, preferences, folderNames ->
        val dbSubredditNames =
            localSubredditsRepository.getDbSubredditsList().map { it.name }.toSet()
        val dbImages = localImagesRepository.getDbImages().toNetworkIdToDbImageMap()

        val after = data.nextPageId?.takeIf { !params.reload }
        val networkImages: NetworkImages =
            if (params.subreddit != null && params.query.isNullOrBlank()) {
                // view subreddit results
                when (params.sortOrder) {
                    SortOrder.HOT -> networkImagesRepository.getHotImages(
                        subreddit = params.subreddit,
                        after = after,
                        includeOver18 = preferences.allowNsfw
                    )
                    SortOrder.NEW -> networkImagesRepository.getNewImages(
                        subreddit = params.subreddit,
                        after = after,
                        includeOver18 = preferences.allowNsfw
                    )
                    else -> networkImagesRepository.getTopImages(
                        subreddit = params.subreddit,
                        timeFilter = params.sortOrder.toTimeFilter(),
                        after = after,
                        includeOver18 = preferences.allowNsfw
                    )
                }
            } else if (params.subreddit == null && params.query != null) {
                // search all
                networkImagesRepository.searchAllImages(
                    query = params.query,
                    sort = getSort(params.sortOrder),
                    time = params.sortOrder.toTimeFilter(),
                    after = after,
                    includeOver18 = preferences.allowNsfw
                )
            } else if (params.subreddit != null && params.query != null) {
                // search subreddit
                networkImagesRepository.searchImagesInSubreddit(
                    subreddit = params.subreddit,
                    query = params.query,
                    sort = getSort(params.sortOrder),
                    time = params.sortOrder.toTimeFilter(),
                    after = after,
                    includeOver18 = preferences.allowNsfw
                )
            } else {
                NetworkImages(nextPageId = "")
            }

        val domainImages = networkImages.images.map {
            it.toDomainImage(
                previewResolution = preferences.previewResolution,
                dbImage = dbImages.getFirstDbImageInAlbum(it.id)
            )
        }

        val domainSubreddit = params.subreddit?.takeIf { data.subreddit == null }?.let {
            networkSubredditsRepository.getSubredditDetail(it).let { networkSubreddit ->
                networkSubreddit.toDomainSubreddit(
                    isSaved = dbSubredditNames.contains(networkSubreddit.name),
                )
            }
        } ?: data.subreddit

        FeedResult(
            images = domainImages,
            subreddit = domainSubreddit,
            nextPageId = networkImages.nextPageId,
            folderNames = folderNames,
            usePresetFolderWhenLiking = preferences.usePresetFolderWhenLiking
        )
    }

    private fun getSort(sortOrder: SortOrder) = when (sortOrder) {
        SortOrder.NEW, SortOrder.HOT -> sortOrder.name.lowercase()
        else -> "top"
    }

    data class Params(
        val subreddit: String? = null,
        val query: String? = null,
        val sortOrder: SortOrder = SortOrder.HOT,
        val reload: Boolean = false
    )
}