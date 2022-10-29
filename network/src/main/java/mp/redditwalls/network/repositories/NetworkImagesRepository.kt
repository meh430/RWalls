package mp.redditwalls.network.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.network.models.TimeFilter
import mp.redditwalls.network.services.ImagesService

class NetworkImagesRepository @Inject constructor(private val imagesService: ImagesService) {
    suspend fun getHotImages(subreddit: String, after: String) = withContext(Dispatchers.IO) {
        imagesService.getHotImages(
            subreddit = subreddit,
            after = after
        )
    }

    suspend fun getNewImages(subreddit: String, after: String) = withContext(Dispatchers.IO) {
        imagesService.getNewImages(
            subreddit = subreddit,
            after = after
        )
    }

    suspend fun getTopImages(
        subreddit: String,
        timeFilter: TimeFilter,
        after: String
    ) = withContext(Dispatchers.IO) {
        imagesService.getTopImages(
            subreddit = subreddit,
            time = timeFilter.value,
            after = after
        )
    }

    suspend fun searchAllImages(
        query: String,
        sort: String,
        time: TimeFilter,
        after: String
    ) = withContext(Dispatchers.IO) {
        imagesService.searchAllImages(
            query = query,
            sort = sort,
            time = time.value,
            after = after
        )
    }

    suspend fun searchImagesInSubreddit(
        subreddit: String,
        query: String,
        sort: String,
        time: TimeFilter,
        after: String
    ) = withContext(Dispatchers.IO) {
        imagesService.searchImagesInSubreddit(
            subreddit = subreddit,
            query = query,
            sort = sort,
            time = time.value,
            after = after
        )
    }

    suspend fun getImage(id: String) = withContext(Dispatchers.IO) {
        imagesService.getImage(id = id)
    }
}