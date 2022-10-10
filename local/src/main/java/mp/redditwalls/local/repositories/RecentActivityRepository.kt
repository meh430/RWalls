package mp.redditwalls.local.repositories

import androidx.room.Query
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

    suspend fun deleteDbRecentActivityItem(dbRecentActivityItem: DbRecentActivityItem) {
        withContext(Dispatchers.IO) {
            dbRecentActivityItemDao.deleteDbRecentActivityItem(dbRecentActivityItem)
        }
    }

    suspend fun deleteDbRecentActivityItems(dbRecentActivityItems: List<DbRecentActivityItem>) {
        withContext(Dispatchers.IO) {
            dbRecentActivityItemDao.deleteDbRecentActivityItems(dbRecentActivityItems)
        }
    }

    fun getDbRecentActivityItems() = dbRecentActivityItemDao.getDbRecentActivityItems()

    @Query("SELECT * FROM RecentActivity ORDER BY createdAt DESC LIMIT :limit")
    fun getLimitedDbRecentActivityItems(limit: Int) =
        dbRecentActivityItemDao.getLimitedDbRecentActivityItems(limit)
}