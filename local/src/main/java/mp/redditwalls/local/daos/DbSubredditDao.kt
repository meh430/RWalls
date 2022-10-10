package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.models.DbSubreddit

@Dao
interface DbSubredditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbSubreddit(dbSubreddit: DbSubreddit)

    @Update
    suspend fun updateDbSubreddits(dbSubreddits: List<DbSubreddit>)

    @Delete
    suspend fun deleteDbSubreddit(dbImage: DbImage)

    @Query("SELECT * FROM SavedSubreddits")
    fun getDbSubreddits(): Flow<List<DbSubreddit>>
}