package mp.redditwalls.network.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.network.models.TimeFilter
import mp.redditwalls.network.services.ImagesService

class NetworkImagesRepository @Inject constructor(private val imagesService: ImagesService) {
    suspend fun getHotImages(subreddit: String) = withContext(Dispatchers.IO) {
        imagesService.getHotImages(subreddit)
    }

    suspend fun getNewImages(subreddit: String) = withContext(Dispatchers.IO) {
        imagesService.getNewImages(subreddit)
    }

    suspend fun getTopImages(
        subreddit: String,
        timeFilter: TimeFilter
    ) = withContext(Dispatchers.IO) {
        imagesService.getTopImages(subreddit, timeFilter.value)
    }

    suspend fun searchAllImages(query: String) = withContext(Dispatchers.IO) {
        imagesService.searchAllImages(query)
    }

    suspend fun searchImagesInSubreddit(
        subreddit: String,
        query: String
    ) = withContext(Dispatchers.IO) {
        imagesService.searchImagesInSubreddit(subreddit, query)
    }

    suspend fun getImage(id: String) = withContext(Dispatchers.IO) {
        imagesService.getImage(id)
    }
}