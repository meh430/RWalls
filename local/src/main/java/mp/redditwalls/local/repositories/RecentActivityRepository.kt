package mp.redditwalls.local.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.local.daos.DbRecentActivityItemDao
import mp.redditwalls.local.models.DbRecentActivityItem

class RecentActivityRepository @Inject constructor(
    private val dbRecentActivityItemDao: DbRecentActivityItemDao
) {
    suspend fun insertDbRecentActivityItem(dbRecentActivityItem: DbRecentActivityItem) {
        withContext(Dispatchers.IO) {
            dbRecentActivityItemDao.insertDbRecentActivityItem(dbRecentActivityItem)
        }
    }

    suspend fun deleteDbRecentActivityItem(ids: List<Int>) = withContext(Dispatchers.IO) {
        dbRecentActivityItemDao.deleteDbRecentActivityItem(ids)
    }

    suspend fun deleteAllDbRecentActivityItem() = withContext(Dispatchers.IO) {
        dbRecentActivityItemDao.deleteAllDbRecentActivityItem()
    }

    fun getDbRecentActivityItems() = dbRecentActivityItemDao.getDbRecentActivityItems()

    fun getLimitedDbRecentActivityItems(limit: Int) =
        dbRecentActivityItemDao.getLimitedDbRecentActivityItems(limit)
}