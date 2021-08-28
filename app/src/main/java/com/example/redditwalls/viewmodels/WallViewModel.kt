package com.example.redditwalls.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.models.PostInfo
import com.example.redditwalls.models.Resource
import com.example.redditwalls.repositories.RWRepository
import com.example.redditwalls.repositories.Resolution
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WallViewModel @Inject constructor(
    private val rwRepository: RWRepository,
    private val imageLoader: ImageLoader
) : BaseViewModel() {
    val postInfo = MutableLiveData<Resource<PostInfo>>()

    init {
        postInfo.value = Resource.loading()
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
}