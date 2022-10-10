package mp.redditwalls.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import javax.inject.Inject
import mp.redditwalls.datasources.ImagesPagingDataSource
import mp.redditwalls.datasources.RWApi

class RWRepository @Inject constructor(
    private val api: RWApi
) {

    fun getImages(
        subreddit: String,
        query: String = "",
        sort: RWApi.Sort
    ) =
        Pager(
            config = PagingConfig(
                pageSize = RWApi.PAGE_SIZE,
                prefetchDistance = 10,
                initialLoadSize = RWApi.PAGE_SIZE
            ),
            pagingSourceFactory = {
                ImagesPagingDataSource(
                    api = api,
                    subreddit = subreddit,
                    query = query,
                    sort = sort
                )
            }
        ).liveData

    suspend fun searchSubs(query: String) = api.searchSubs(query)

    suspend fun getPostInfo(postLink: String, imageSize: Double) =
        api.getPostInfo(postLink, imageSize)


    suspend fun getImageFromPost(postLink: String = "", subreddit: String = "", id: String = "") =
        api.getImageFromPost(postLink = postLink, subreddit = subreddit, id = id)

}