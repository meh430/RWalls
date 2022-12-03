package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class SubredditItemUiState(
    val name: String = "",
    val numSubscribers: Int = 0,
    val numOnline: Int = 0,
    val description: String = "",
    val subredditIconUrl: String = "",
    val isSaved: MutableState<Boolean> = mutableStateOf(false)
)
