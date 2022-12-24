package mp.redditwalls.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import mp.redditwalls.domain.models.RecommendedSubreddit

data class RecommendedSubredditUiState(
    val subredditItemUiState: SubredditItemUiState,
    val images: SnapshotStateList<ImageItemUiState> = mutableStateListOf()
)

fun RecommendedSubreddit.toRecommendedSubredditUiState() = RecommendedSubredditUiState(
    subredditItemUiState = subreddit.toSubredditItemUiState(),
    images = mutableStateListOf<ImageItemUiState>().apply {
        addAll(images.map { it.toImageItemUiState() })
    }
)