package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import mp.redditwalls.domain.models.DomainSubreddit
import mp.redditwalls.network.Constants

data class SubredditItemUiState(
    val name: String = "",
    val numSubscribers: Int = 0,
    val numOnline: Int = 0,
    val description: String = "",
    val subredditIconUrl: String = "",
    val headerUrl: String = "",
    val isSaved: MutableState<Boolean> = mutableStateOf(false)
) {
    val subredditUrl: String
        get() = "${Constants.BASE_REDDIT_URL}/r/$name"
}

fun DomainSubreddit.toSubredditItemUiState() = SubredditItemUiState(
    name = name,
    numSubscribers = numSubscribers,
    numOnline = numOnline,
    description = description,
    subredditIconUrl = subredditIconUrl,
    headerUrl = headerUrl,
    isSaved = mutableStateOf(isSaved)
)