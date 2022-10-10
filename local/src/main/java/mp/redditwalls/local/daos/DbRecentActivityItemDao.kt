package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbRecentActivityItem

@Dao
interface DbRecentActivityItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbRecentActivityItem(dbRecentActivityItem: DbRecentActivityItem)

    @Delete
    suspend fun deleteDbRecentActivityItem(dbRecentActivityItem: DbRecentActivityItem)

    @Delete
    suspend fun deleteDbRecentActivityItems(dbRecentActivityItems: List<DbRecentActivityItem>)

    @Query("SELECT * FROM RecentActivity ORDER BY createdAt DESC")
    fun getDbRecentActivityItems(): Flow<List<DbRecentActivityItem>>

    @Query("SELECT * FROM RecentActivity ORDER BY createdAt DESC LIMIT :limit")
    fun getLimitedDbRecentActivityItems(limit: Int): Flow<List<DbRecentActivityItem>>
}
