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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbImages(dbImages: List<DbImage>)

    @Query("UPDATE FavoriteImages SET imageFolderName = :imageFolderName WHERE id = :id")
    suspend fun updateDbImageFolder(id: String, imageFolderName: String)

    @Query("UPDATE FavoriteImages SET imageFolderName = :imageFolderName WHERE id IN (:ids)")
    suspend fun updateDbImagesFolder(ids: List<String>, imageFolderName: String)

    @Query("SELECT EXISTS(SELECT * FROM FavoriteImages WHERE id = :networkId)")
    fun dbImageExists(networkId : String) : Boolean

    @Query("DELETE FROM FavoriteImages WHERE id = :id")
    suspend fun deleteDbImage(id: String)

    @Query("DELETE FROM FavoriteImages WHERE id IN (:ids)")
    suspend fun deleteDbImages(ids: List<String>)

    @Query("SELECT * FROM FavoriteImages")
    fun getDbImages(): Flow<List<DbImage>>
}