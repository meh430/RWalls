package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import mp.redditwalls.domain.models.DomainSubreddit

data class SubredditItemUiState(
    val name: String = "",
    val numSubscribers: Int = 0,
    val numOnline: Int = 0,
    val description: String = "",
    val subredditIconUrl: String = "",
    val headerUrl: String = "",
    val isSaved: MutableState<Boolean> = mutableStateOf(false)
)

fun DomainSubreddit.toSubredditItemUiState() = SubredditItemUiState(
    name = name,
    numSubscribers = numSubscribers,
    numOnline = numOnline,
    description = description,
    subredditIconUrl = subredditIconUrl,
    headerUrl = headerUrl,
    isSaved = mutableStateOf(isSaved)
)