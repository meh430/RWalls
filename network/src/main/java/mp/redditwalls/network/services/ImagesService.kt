package mp.redditwalls.network.services

import mp.redditwalls.network.models.NetworkImage
import mp.redditwalls.network.models.NetworkImages
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ImagesService {
    @GET("/r/{subreddit}/hot")
    suspend fun getHotImages(@Path("subreddit") subreddit: String): NetworkImages

    @GET("/r/{subreddit}/new")
    suspend fun getNewImages(@Path("subreddit") subreddit: String): NetworkImages

    @GET("/r/{subreddit}/top")
    suspend fun getTopImages(
        @Path("subreddit") subreddit: String,
        @Query("t") time: String
    ): NetworkImages

    @GET("/search")
    suspend fun searchAllImages(@Query("q") query: String): NetworkImages

    @GET("/r/{subreddit}/search")
    suspend fun searchImagesInSubreddit(
        @Path("subreddit") subreddit: String,
        @Query("q") query: String,
        @Query("restrict_sr") restrictSubreddit: Boolean = true
    ): NetworkImages

    @GET("/api/info")
    suspend fun getImage(@Query("id") id: String): NetworkImage
}