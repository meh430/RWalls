package com.example.redditwalls.datasources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.redditwalls.models.Image

class ImagesPagingDataSource(
    private val subreddit: String = "",
    private val query: String = "",
    private val sort: RWApi.Sort
) : PagingSource<String, Image>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Image> {
        val after = params.key ?: ""

        return try {
            val result = RWApi.getImages(subreddit, query, sort, after)
            LoadResult.Page(
                data = result.first,
                prevKey = null,
                nextKey = result.second.takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Image>): String {
        return ""
    }
}