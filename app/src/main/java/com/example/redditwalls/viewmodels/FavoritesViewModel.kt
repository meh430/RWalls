package com.example.redditwalls.viewmodels

import androidx.lifecycle.*
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
}