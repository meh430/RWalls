package mp.redditwalls.domain.models

import mp.redditwalls.network.models.NetworkSubreddit

data class DomainSubreddit(
    val name: String = "",
    val numSubscribers: Int = 0,
    val numOnline: Int = 0,
    val description: String = "",
    val subredditIconUrl: String = "",
    val headerUrl: String = "",
    val isSaved: Boolean = false
)

fun NetworkSubreddit.toDomainSubreddit(isSaved: Boolean) = DomainSubreddit(
    name = name,
    numSubscribers = numSubscribers,
    numOnline = numOnline,
    description = description,
    subredditIconUrl = subredditIconUrl,
    headerUrl = headerImageUrl,
    isSaved = isSaved
)