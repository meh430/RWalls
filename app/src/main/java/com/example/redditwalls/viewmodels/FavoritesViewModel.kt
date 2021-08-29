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

    fun deleteAllFavorites() = viewModelScope.launch {
        favoriteImagesRepository.deleteAllFavorites()
    }

    fun getRandomFavoriteImage(onClick: (Image) -> Unit) {
        viewModelScope.launch {
            val randomImage = favoriteImagesRepository.getRandomFavoriteImage()
            onClick(randomImage)
        }
    }

    // True if added, false if not
    suspend fun addFavorite(image: Image): Boolean {
        return !favoriteImagesRepository.favoriteExists(image.imageLink).also {
            if (it) {
                favoriteImagesRepository.deleteFavoriteImage(image)
            } else {
                favoriteImagesRepository.insertFavorite(image)
            }
        }
    }
}