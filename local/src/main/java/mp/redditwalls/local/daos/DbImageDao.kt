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

    @Query("UPDATE FavoriteImages SET refreshLocation = :refreshLocation WHERE networkId = :id")
    suspend fun updateDbImageRefreshLocation(id: String, refreshLocation: String)

    @Query("SELECT EXISTS(SELECT * FROM FavoriteImages WHERE networkId = :networkId)")
    fun dbImageExists(networkId : String) : Boolean

    @Query("DELETE FROM FavoriteImages WHERE networkId = :id")
    suspend fun deleteDbImage(id: String)

    @Query("DELETE FROM FavoriteImages WHERE networkId IN (:ids)")
    suspend fun deleteDbImages(ids: List<String>)

    @Query("SELECT * FROM FavoriteImages")
    fun getDbImages(): Flow<List<DbImage>>

    @Query("SELECT * FROM FavoriteImages WHERE refreshLocation IN (:locations) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDbImage(locations: List<String>): DbImage
}