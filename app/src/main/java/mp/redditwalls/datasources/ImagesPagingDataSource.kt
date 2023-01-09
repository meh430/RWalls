package mp.redditwalls.datasources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import mp.redditwalls.models.Image

class ImagesPagingDataSource(
    private val api: RWApi,
    private val subreddit: String = "",
    private val query: String = "",
    private val sort: RWApi.Sort
) : PagingSource<String, Image>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Image> {
        val after = params.key ?: ""

        return try {
            val result = api.getImages(subreddit, query, sort, after)
            val nextKey = result.second.takeIf { it.isNotBlank() }
            LoadResult.Page(
                data = result.first,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Image>): String {
        return ""
    }
}