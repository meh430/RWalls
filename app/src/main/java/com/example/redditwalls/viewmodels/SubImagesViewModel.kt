package com.example.redditwalls.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubImagesViewModel @Inject constructor(private val rwRepository: RWRepository) : ViewModel() {

    fun getImages(subreddit: String, sort: RWApi.Sort) =
        rwRepository.getImages(subreddit, sort).cachedIn(viewModelScope)

}