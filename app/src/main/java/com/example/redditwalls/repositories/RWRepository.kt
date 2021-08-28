package com.example.redditwalls.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.redditwalls.datasources.ImagesPagingDataSource
import com.example.redditwalls.datasources.RWApi
import javax.inject.Inject

class RWRepository @Inject constructor(private val api: RWApi) {

    fun getImages(subreddit: String, query: String = "", sort: RWApi.Sort) =
        Pager(
            config = PagingConfig(
                pageSize = RWApi.PAGE_SIZE,
                prefetchDistance = 2,
                initialLoadSize = RWApi.PAGE_SIZE
            ),
            pagingSourceFactory = { ImagesPagingDataSource(api, subreddit, query, sort) }
        ).liveData

    suspend fun searchSubs(query: String) = api.searchSubs(query)

    suspend fun getPostInfo(postLink: String, imageSize: Double) =
        api.getPostInfo(postLink, imageSize)
}