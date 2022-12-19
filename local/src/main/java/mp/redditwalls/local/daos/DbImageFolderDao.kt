package mp.redditwalls.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.local.models.DbImageFolder
import mp.redditwalls.local.models.DbImageFolderWithImages

@Dao
interface DbImageFolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDbImageFolder(dbImageFolder: DbImageFolder)

    @Query("DELETE FROM ImageFolders WHERE name = :name")
    suspend fun deleteDbImageFolder(name: String)

    @Query("UPDATE ImageFolders SET refreshEnabled = :refreshEnabled WHERE name = :name")
    suspend fun updateDbImageRefreshEnabled(name: String, refreshEnabled: Boolean)

    @Query("UPDATE ImageFolders SET refreshLocation = :refreshLocation WHERE name = :name")
    suspend fun updateDbImageRefreshLocation(name: String, refreshLocation: String)

    @Query("SELECT name FROM ImageFolders")
    fun getDbImageFolderNames(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM ImageFolders WHERE name = :name")
    fun getDbImageFolderWithImages(name: String): Flow<DbImageFolderWithImages>

    @Query("SELECT name FROM ImageFolders WHERE refreshEnabled = 1 AND refreshLocation in (:refreshLocations)")
    suspend fun getRefreshEnabledDbImageFolderNames(refreshLocations: List<String>): List<String>

    @Query("SELECT * FROM FavoriteImages WHERE imageFolderName IN (:folderNames) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDbImageFromFolders(folderNames: List<String>): DbImage?

    @Transaction
    suspend fun getRandomDbImage(refreshLocations: List<String>): DbImage? {
        return getRandomDbImageFromFolders(getRefreshEnabledDbImageFolderNames(refreshLocations))
    }
}