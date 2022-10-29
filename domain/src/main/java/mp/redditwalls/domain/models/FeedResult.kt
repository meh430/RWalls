package mp.redditwalls.domain.models

data class FeedResult(
    val images: List<DomainImage> = emptyList(),
    val nextPageId: String = ""
)