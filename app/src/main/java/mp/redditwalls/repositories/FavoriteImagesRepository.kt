package mp.redditwalls.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.datasources.FavoritesDAO
import mp.redditwalls.models.Image

class FavoriteImagesRepository @Inject constructor(private val favoritesDAO: FavoritesDAO) {
    suspend fun insertFavorite(favorite: Image) = withContext(Dispatchers.IO) {
        favoritesDAO.insertFavorite(favorite)
    }

    suspend fun deleteAllFavorites() = withContext(Dispatchers.IO) {
        favoritesDAO.deleteAllFavorites()
    }

    suspend fun deleteFavoriteImage(imageLink: String) = withContext(Dispatchers.IO) {
        favoritesDAO.deleteFavoriteImage(imageLink)
    }

    fun getFavoritesFlow() = favoritesDAO.getFavoritesFlow()

    suspend fun getFavorites() = withContext(Dispatchers.IO) {
        favoritesDAO.getFavorites()
    }

    suspend fun favoriteExists(imageLink: String) = withContext(Dispatchers.IO) {
        favoritesDAO.favoriteExists(imageLink)
    }

    suspend fun getRandomFavoriteImage() = withContext(Dispatchers.IO) {
        favoritesDAO.getRandomFavoriteImage()
    }

    suspend fun getFavoriteImage(index: Int) = withContext(Dispatchers.IO) {
        favoritesDAO.getLimitedFavoriteImages(index)
    }

    suspend fun getFavoriteImagesCount() = withContext(Dispatchers.IO) {
        favoritesDAO.getFavoriteImagesCount()
    }
}