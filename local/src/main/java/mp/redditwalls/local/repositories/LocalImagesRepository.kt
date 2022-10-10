package mp.redditwalls.local.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import mp.redditwalls.local.daos.DbImageDao
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.models.DbImage

class LocalImagesRepository @Inject constructor(private val dbImageDao: DbImageDao) {
    suspend fun insertDbImage(dbImage: DbImage) {
        withContext(Dispatchers.IO) {
            dbImageDao.insertDbImage(dbImage)
        }
    }

    suspend fun updateDbImage(dbImage: DbImage) {
        withContext(Dispatchers.IO) {
            dbImageDao.updateDbImage(dbImage)
        }
    }

    suspend fun deleteDbImage(dbImage: DbImage) {
        withContext(Dispatchers.IO) {
            dbImageDao.deleteDbImage(dbImage)
        }
    }

    suspend fun deleteDbImages(dbImages: List<DbImage>) {
        withContext(Dispatchers.IO) {
            dbImageDao.deleteDbImages(dbImages)
        }
    }

    fun getDbImagesFlow() = dbImageDao.getDbImages()

    suspend fun getDbImages() = dbImageDao.getDbImages().firstOrNull().orEmpty()

    suspend fun getRandomLockScreenDbImage() = withContext(Dispatchers.IO) {
        dbImageDao.getRandomDbImage(
            listOf(
                WallpaperLocation.LOCK_SCREEN.name,
                WallpaperLocation.BOTH.name
            )
        )
    }

    suspend fun getRandomHomeScreenDbImage() = withContext(Dispatchers.IO) {
        dbImageDao.getRandomDbImage(
            listOf(
                WallpaperLocation.HOME.name,
                WallpaperLocation.BOTH.name
            )
        )
    }
}