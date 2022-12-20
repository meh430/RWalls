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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDbImageFolder(dbImageFolder: DbImageFolder)

    @Query("DELETE FROM ImageFolders WHERE name = :name")
    suspend fun deleteDbImageFolder(name: String)

    @Query("DELETE FROM FavoriteImages WHERE imageFolderName = :name")
    suspend fun deleteDbImagesInFolder(name: String)

    @Transaction
    suspend fun deleteDbImageFolderAndDbImages(name: String) {
        deleteDbImageFolder(name)
        deleteDbImagesInFolder(name)
    }

    @Query("UPDATE ImageFolders SET refreshEnabled = :refreshEnabled, refreshLocation = :refreshLocation WHERE name = :name")
    suspend fun updateDbImageFolderSettings(
        name: String,
        refreshEnabled: Boolean,
        refreshLocation: String
    )

    @Query("SELECT name FROM ImageFolders ORDER BY createdAt ASC")
    fun getDbImageFolderNames(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM ImageFolders WHERE name = :name")
    fun getDbImageFolderWithImages(name: String): DbImageFolderWithImages

    @Query("SELECT name FROM ImageFolders WHERE refreshEnabled = 1 AND refreshLocation in (:refreshLocations)")
    suspend fun getRefreshEnabledDbImageFolderNames(refreshLocations: List<String>): List<String>

    @Query("SELECT * FROM FavoriteImages WHERE imageFolderName IN (:folderNames) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDbImageFromFolders(folderNames: List<String>): DbImage?

    @Transaction
    suspend fun getRandomDbImage(refreshLocations: List<String>): DbImage? {
        return getRandomDbImageFromFolders(getRefreshEnabledDbImageFolderNames(refreshLocations))
    }

    @Query("SELECT EXISTS(SELECT * FROM ImageFolders WHERE name = :name)")
    suspend fun dbImageFolderExists(name: String): Boolean
}