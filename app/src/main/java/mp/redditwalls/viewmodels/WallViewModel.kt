package mp.redditwalls.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.WallpaperLocation
import mp.redditwalls.utils.ImageLoader
import mp.redditwalls.utils.Utils
import mp.redditwalls.models.History
import mp.redditwalls.models.Image
import mp.redditwalls.models.PostInfo
import mp.redditwalls.models.Resolution
import mp.redditwalls.models.Resource
import mp.redditwalls.repositories.FavoriteImagesRepository
import mp.redditwalls.repositories.HistoryRepository
import mp.redditwalls.repositories.RWRepository

@HiltViewModel
class WallViewModel @Inject constructor(
    private val rwRepository: RWRepository,
    private val favoriteImagesRepository: FavoriteImagesRepository,
    private val historyRepository: HistoryRepository,
    private val imageLoader: ImageLoader
) : BaseViewModel() {
    val postInfo = MutableLiveData<Resource<PostInfo>>()
    val isFavorite = MutableLiveData<Boolean>()
    val currentImage = MutableLiveData<Resource<Image>>()

    init {
        postInfo.value = Resource.loading()
    }

    fun initialize(image: Image?, postId: String?, subreddit: String?) {
        viewModelScope.launch {
            if (image != null) {
                currentImage.postValue(Resource.success(image))
            } else {
                getResource(currentImage) {
                    rwRepository.getImageFromPost(id = postId ?: "", subreddit = subreddit ?: "")
                }
            }
        }
    }

    fun toggleFavorite() {
        isFavorite.value?.let {
            viewModelScope.launch {
                currentImage.value?.data?.let { image ->
                    favoriteImagesRepository.favoriteExists(image.imageLink).let { exists ->
                        if (exists) {
                            favoriteImagesRepository.deleteFavoriteImage(image.imageLink)
                        } else {
                            favoriteImagesRepository.insertFavorite(image)
                        }

                        isFavorite.postValue(!exists)
                    }
                }

            }
        }
    }

    fun getPostInfo(
        postLink: String,
        imageLink: String,
        context: Context,
        resolution: Resolution
    ) {
        getResource(postInfo) {
            val imageSize = imageLoader.getImageSize(context, imageLink, resolution)
            PostInfo(rwRepository.getPostInfo(postLink, imageSize), Utils.getResolution(context))
        }
    }

    fun setUpFavorite(image: Image) {
        viewModelScope.launch {
            isFavorite.postValue(favoriteImagesRepository.favoriteExists(image.imageLink))
        }
    }

    fun insertHistory(location: WallpaperLocation) {
        viewModelScope.launch {
            val image = currentImage.value?.data
            val info = postInfo.value?.data

            if (image != null && info != null) {
                historyRepository.insertHistory(
                    History(
                        image = image.copy(subreddit = info.subreddit),
                        dateCreated = Date().time,
                        true,
                        location = location.id
                    )
                )
            }
        }
    }
}