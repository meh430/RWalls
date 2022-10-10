package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbImage

@Dao
interface DbImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbImage(dbImage: DbImage)

    @Update
    suspend fun updateDbImage(dbImage: DbImage)

    @Delete
    suspend fun deleteDbImage(dbImage: DbImage)

    @Delete
    suspend fun deleteDbImages(dbImages: List<DbImage>)

    @Query("SELECT * FROM FavoriteImages")
    fun getDbImages(): Flow<List<DbImage>>

    @Query("SELECT * FROM FavoriteImages WHERE refreshLocation in (:locations) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDbImage(locations: List<String>): DbImage
}