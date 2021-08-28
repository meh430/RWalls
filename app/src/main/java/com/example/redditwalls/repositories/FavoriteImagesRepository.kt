package com.example.redditwalls.repositories

import com.example.redditwalls.datasources.FavoritesDAO
import com.example.redditwalls.models.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteImagesRepository @Inject constructor(private val favoritesDAO: FavoritesDAO) {
    suspend fun insertFavorite(favorite: Image) = withContext(Dispatchers.IO) {
        favoritesDAO.insertFavorite(favorite)
    }

    suspend fun deleteAllFavorites() = withContext(Dispatchers.IO) {
        favoritesDAO.deleteAllFavorites()
    }

    suspend fun deleteFavoriteImage(favorite: Image) = withContext(Dispatchers.IO) {
        favoritesDAO.deleteFavoriteImage(favorite)
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
}