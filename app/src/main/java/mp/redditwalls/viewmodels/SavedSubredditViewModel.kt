package mp.redditwalls.viewmodels

import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.domain.usecases.DeleteSubredditUseCase
import mp.redditwalls.domain.usecases.SaveSubredditUseCase
import mp.redditwalls.models.SubredditItemUiState

class SavedSubredditViewModel @Inject constructor(
    private val saveSubredditUseCase: SaveSubredditUseCase,
    private val deleteSubredditUseCase: DeleteSubredditUseCase
) : DelegateViewModel() {
    private fun saveSubreddit(subredditName: String) {
        coroutineScope.launch {
            saveSubredditUseCase(subredditName)
        }
    }

    private fun removeSubreddit(subredditName: String) {
        coroutineScope.launch {
            deleteSubredditUseCase(subredditName)
        }
    }

    fun onSaveClick(subreddit: SubredditItemUiState, isSaved: Boolean) {
        subreddit.isSaved.value = isSaved
        if (isSaved) {
            saveSubreddit(subreddit.name)
        } else {
            removeSubreddit(subreddit.name)
        }
    }
}