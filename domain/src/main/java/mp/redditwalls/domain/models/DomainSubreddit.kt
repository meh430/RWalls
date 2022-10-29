package mp.redditwalls.domain.models

import mp.redditwalls.network.models.NetworkSubreddit

data class DomainSubreddit(
    val name: String,
    val numSubscribers: Int,
    val numOnline: Int,
    val description: String,
    val subredditIconUrl: String = "",
    val isSaved: Boolean,
    val dbId: Int
)

fun NetworkSubreddit.toDomainSubreddit(isSaved: Boolean, dbId: Int) = DomainSubreddit(
    name = name,
    numSubscribers = numSubscribers,
    numOnline = numOnline,
    description = description,
    subredditIconUrl = subredditIconUrl,
    isSaved = isSaved,
    dbId = dbId
)