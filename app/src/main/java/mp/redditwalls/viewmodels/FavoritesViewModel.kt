package mp.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.models.Image
import mp.redditwalls.repositories.FavoriteImagesRepository

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

    suspend fun getFavoritesCount() = favoriteImagesRepository.getFavoriteImagesCount()
}