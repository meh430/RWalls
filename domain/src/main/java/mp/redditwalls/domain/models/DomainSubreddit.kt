package mp.redditwalls.domain.models

data class DomainSubreddit(
    val name: String,
    val numSubscribers: Int,
    val numOnline: Int,
    val description: String,
    val subredditIconUrl: String = ""
)