package mp.redditwalls.network.services

import mp.redditwalls.network.models.NetworkSubreddit
import mp.redditwalls.network.models.NetworkSubreddits
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SubredditsService {
    @GET("/r/{subreddit}/about")
    suspend fun getSubredditInfo(@Path("subreddit") subreddit: String): NetworkSubreddit

    @GET("/api/subreddit_autocomplete_v2")
    suspend fun searchSubreddits(
        @Query("query") query: String,
        @Query("include_over_18") includeOver18: Boolean
    ): NetworkSubreddits
}