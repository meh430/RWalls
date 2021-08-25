package com.example.redditwalls.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.models.PostInfo
import com.example.redditwalls.models.Resource
import com.example.redditwalls.repositories.RWRepository
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

    fun getPostInfo(postLink: String, imageSize: Int) {
        getResource(postInfo) {
            rwRepository.getPostInfo(postLink, imageSize)
        }
    }
}