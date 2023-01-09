package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbRecentActivityItem

@Dao
interface DbRecentActivityItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbRecentActivityItem(dbRecentActivityItem: DbRecentActivityItem)

    @Query("DELETE FROM RecentActivity WHERE id IN (:ids)")
    suspend fun deleteDbRecentActivityItem(ids: List<Int>)

    @Query("DELETE FROM RecentActivity")
    suspend fun deleteAllDbRecentActivityItem()

    @Query("SELECT * FROM RecentActivity ORDER BY createdAt DESC")
    fun getDbRecentActivityItems(): Flow<List<DbRecentActivityItem>>

    @Query("SELECT * FROM RecentActivity ORDER BY createdAt DESC LIMIT :limit")
    fun getLimitedDbRecentActivityItems(limit: Int): Flow<List<DbRecentActivityItem>>
}
