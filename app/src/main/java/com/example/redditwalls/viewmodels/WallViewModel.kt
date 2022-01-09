package com.example.redditwalls.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.models.*
import com.example.redditwalls.repositories.FavoriteImagesRepository
import com.example.redditwalls.repositories.HistoryRepository
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

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
            currentImage.value?.data?.let {
                historyRepository.insertHistory(
                    History(
                        image = it,
                        dateCreated = Date().time,
                        true,
                        location = location.id
                    )
                )
            }
        }
    }
}