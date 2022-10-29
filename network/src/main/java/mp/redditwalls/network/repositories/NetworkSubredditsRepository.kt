package mp.redditwalls.network.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.network.services.SubredditsService

class NetworkSubredditsRepository @Inject constructor(private val subredditsService: SubredditsService) {
    suspend fun getSubredditDetail(subreddit: String) = withContext(Dispatchers.IO) {
        subredditsService.getSubredditDetail(subreddit)
    }

    suspend fun getSubredditsInfo(subreddits: List<String>) = withContext(Dispatchers.IO) {
        subredditsService.getSubredditsInfo(subreddits.joinToString { "," })
    }

    suspend fun searchSubreddits(
        query: String,
        includeOver18: Boolean
    ) = withContext(Dispatchers.IO) {
        subredditsService.searchSubreddits(query, includeOver18)
    }
}