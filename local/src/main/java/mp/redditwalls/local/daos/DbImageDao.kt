package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbImage

@Dao
interface DbImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbImage(dbImage: DbImage)

    @Query("UPDATE FavoriteImages SET refreshLocation = :refreshLocation WHERE id = :id")
    suspend fun updateDbImageRefreshLocation(id: Int, refreshLocation: String)

    @Query("DELETE FROM FavoriteImages WHERE id = :id")
    suspend fun deleteDbImage(id: Int)

    @Query("DELETE FROM FavoriteImages WHERE id IN (:ids)")
    suspend fun deleteDbImages(ids: List<Int>)

    @Query("SELECT * FROM FavoriteImages")
    fun getDbImages(): Flow<List<DbImage>>

    @Query("SELECT * FROM FavoriteImages WHERE refreshLocation IN (:locations) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDbImage(locations: List<String>): DbImage
}