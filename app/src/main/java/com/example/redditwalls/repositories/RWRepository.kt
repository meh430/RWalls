package com.example.redditwalls.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.redditwalls.datasources.ImagesPagingDataSource
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.datasources.RWApi.PAGE_SIZE
import javax.inject.Inject

class RWRepository @Inject constructor() {
    fun getImages(subreddit: String, sort: RWApi.Sort) =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 2),
            pagingSourceFactory = { ImagesPagingDataSource(subreddit, sort) }
        ).flow

    suspend fun searchSubs(query: String) = RWApi.searchSubs(query)

    suspend fun getPostInfo(postLink: String, imageSize: Int) =
        RWApi.getPostInfo(postLink, imageSize)
}