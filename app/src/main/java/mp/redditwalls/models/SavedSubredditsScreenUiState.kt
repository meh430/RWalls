package mp.redditwalls.models

data class SavedSubredditsScreenUiState(
    val subreddits: List<SubredditItemUiState> = emptyList(),
    val uiResult: UiResult = UiResult.Loading()
)