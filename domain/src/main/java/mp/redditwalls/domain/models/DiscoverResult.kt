package mp.redditwalls.domain.models

data class DiscoverResult(
    val allowNsfw: Boolean = false,
    val recommendations: List<RecommendedSubreddit> = emptyList(),
    val mostRecentActivities: List<DomainRecentActivityItem> = emptyList(),
    val folderNames: List<String> = emptyList(),
    val usePresetFolderWhenLiking: Boolean = false
)
