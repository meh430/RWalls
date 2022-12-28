package mp.redditwalls.utils

import javax.inject.Inject
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.models.DbSubreddit
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.repositories.FavoriteImagesRepository
import mp.redditwalls.repositories.FavoriteSubredditsRepository

class RwToLocalMigration @Inject constructor(
    private val localImagesRepository: LocalImagesRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val favoriteImagesRepository: FavoriteImagesRepository,
    private val favoriteSubredditsRepository: FavoriteSubredditsRepository
) {
    suspend fun migrate() {
        val images = favoriteImagesRepository.getFavorites().map {
            val arr = it.postLink.split("/")
            val index = arr.indexOf("comments")
            DbImage(
                networkId = "t3_${arr.getOrNull(index + 1)}",
                postTitle = "Image",
                subredditName = it.subreddit,
                postUrl = it.postLink,
                lowQualityUrl = it.previewLink,
                mediumQualityUrl = it.imageLink,
                sourceUrl = it.imageLink
            )
        }
        val subreddits = favoriteSubredditsRepository.getFavoriteSubreddits().map {
            DbSubreddit(name = it.name.removeSubPrefix())
        }

        if (images.isNotEmpty()) {
            localImagesRepository.insertDbImages(images)
        }
        if (subreddits.isNotEmpty()) {
            localSubredditsRepository.insertDbSubreddits(subreddits)
        }
    }
}