package com.example.redditwalls.viewmodels

import androidx.lifecycle.*
import com.example.redditwalls.repositories.FavoriteImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteImagesRepository: FavoriteImagesRepository
) : ViewModel() {

    fun getFavorites() =
        favoriteImagesRepository.getFavoritesFlow().asLiveData(viewModelScope.coroutineContext)
}