package mp.redditwalls.domain.models

data class FeedResult(
    val images: List<DomainImage> = emptyList(),
    val subreddit: DomainSubreddit? = null,
    val nextPageId: String? = null,
    val folderNames: List<String> = emptyList(),
    val usePresetFolderWhenLiking: Boolean = false
)