package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.domain.models.RecommendedSubreddit
import mp.redditwalls.utils.toFriendlyCount

data class RecommendedSubredditUiState(
    val subredditIconUrl: String,
    val subredditName: String,
    val subscriberCount: String,
    val isSaved: MutableState<Boolean> = mutableStateOf(false),
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf()
)

fun RecommendedSubreddit.toRecommendedSubredditUiState() = RecommendedSubredditUiState(
    subredditIconUrl = subreddit.subredditIconUrl,
    subredditName = subreddit.name,
    subscriberCount = subreddit.numSubscribers.toFriendlyCount(),
    isSaved = mutableStateOf(subreddit.isSaved),
    images = mutableStateListOf<ImageItemUiState>().apply {
        addAll(images.map { it.toImageItemItemUiState() })
    }
)