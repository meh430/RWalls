package mp.redditwalls.domain.models

data class RecommendedSubreddit(
    val subreddit: DomainSubreddit,
    val images: List<DomainImage>
)