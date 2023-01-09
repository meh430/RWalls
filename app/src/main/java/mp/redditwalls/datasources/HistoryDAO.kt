package mp.redditwalls.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.models.History

@Dao
interface HistoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History)

    @Query("DELETE FROM History")
    suspend fun deleteHistory()

    @Query("DELETE FROM History WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("SELECT * FROM History ORDER BY dateCreated DESC")
    fun getHistoryFlow(): Flow<List<History>>

    @Query("SELECT * FROM History")
    suspend fun getHistory(): List<History>

    @Query("SELECT COUNT(id) FROM History")
    suspend fun getHistoryCount(): Int
}