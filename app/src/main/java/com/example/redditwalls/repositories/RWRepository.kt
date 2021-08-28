package com.example.redditwalls.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.redditwalls.datasources.ImagesPagingDataSource
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.datasources.RWApi.PAGE_SIZE
import javax.inject.Inject

class RWRepository @Inject constructor() {
    fun getImages(subreddit: String, query: String = "", sort: RWApi.Sort) =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 2,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { ImagesPagingDataSource(subreddit, query, sort) }
        ).liveData

    suspend fun searchSubs(query: String) = RWApi.searchSubs(query)

    suspend fun getPostInfo(postLink: String, imageSize: Double) =
        RWApi.getPostInfo(postLink, imageSize)
}