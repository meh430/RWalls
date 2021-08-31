package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.redditwalls.models.Image
import com.example.redditwalls.repositories.FavoriteImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteImagesRepository: FavoriteImagesRepository
) : ViewModel() {

    fun getFavorites() =
        favoriteImagesRepository.getFavoritesFlow().asLiveData(viewModelScope.coroutineContext)

    suspend fun getFavoritesAsList() = favoriteImagesRepository.getFavorites()

    fun deleteAllFavorites() = viewModelScope.launch {
        favoriteImagesRepository.deleteAllFavorites()
    }

    suspend fun getRandomFavoriteImage() = favoriteImagesRepository.getRandomFavoriteImage()


    // True if added, false if not
    suspend fun addFavorite(image: Image): Boolean {
        val exists = favoriteImagesRepository.favoriteExists(image.imageLink)
        if (exists) {
            favoriteImagesRepository.deleteFavoriteImage(image.imageLink)
        } else {
            favoriteImagesRepository.insertFavorite(image)
        }

        return !exists
    }
}