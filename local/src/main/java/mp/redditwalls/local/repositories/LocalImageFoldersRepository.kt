package mp.redditwalls.local.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.local.daos.DbImageFolderDao
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.models.DbImageFolder

class LocalImageFoldersRepository @Inject constructor(
    private val dbImageFolderDao: DbImageFolderDao
) {
    suspend fun insertDbImageFolder(dbImageFolder: DbImageFolder) = withContext(Dispatchers.IO) {
        dbImageFolderDao.insertDbImageFolder(dbImageFolder)
    }

    suspend fun deleteDbImageFolder(name: String) = withContext(Dispatchers.IO) {
        dbImageFolderDao.deleteDbImageFolder(name)
    }

    suspend fun deleteDbImageFolderAndDmImages(name: String) = withContext(Dispatchers.IO) {
        dbImageFolderDao.deleteDbImageFolderAndDmImages(name)
    }

    suspend fun updateDbImageRefreshEnabled(name: String, refreshEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            dbImageFolderDao.updateDbImageRefreshEnabled(name, refreshEnabled)
        }

    suspend fun updateDbImageRefreshLocation(name: String, refreshLocation: WallpaperLocation) =
        withContext(Dispatchers.IO) {
            dbImageFolderDao.updateDbImageRefreshLocation(name, refreshLocation.name)
        }

    fun getDbImageFolderNames() = dbImageFolderDao.getDbImageFolderNames()


    suspend fun getDbImageFolderWithImages(name: String) = withContext(Dispatchers.IO) {
        dbImageFolderDao.getDbImageFolderWithImages(name)
    }


    suspend fun getRandomLockScreenDbImage() = withContext(Dispatchers.IO) {
        dbImageFolderDao.getRandomDbImage(
            listOf(
                WallpaperLocation.LOCK_SCREEN.name,
                WallpaperLocation.BOTH.name
            )
        )
    }

    suspend fun getRandomHomeScreenDbImage() = withContext(Dispatchers.IO) {
        dbImageFolderDao.getRandomDbImage(
            listOf(
                WallpaperLocation.HOME.name,
                WallpaperLocation.BOTH.name
            )
        )
    }
}