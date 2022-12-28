package mp.redditwalls.local.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import mp.redditwalls.local.daos.DbImageDao
import mp.redditwalls.local.models.DbImage

class LocalImagesRepository @Inject constructor(private val dbImageDao: DbImageDao) {
    suspend fun dbImageExists(id: String) = withContext(Dispatchers.IO) {
        dbImageDao.dbImageExists(id)
    }

    suspend fun insertDbImages(dbImages: List<DbImage>) {
        withContext(Dispatchers.IO) {
            dbImageDao.insertDbImages(dbImages)
        }
    }

    suspend fun insertDbImage(dbImage: DbImage) {
        withContext(Dispatchers.IO) {
            dbImageDao.insertDbImage(dbImage)
        }
    }

    suspend fun updateDbImageFolder(id: String, folderName: String) {
        withContext(Dispatchers.IO) {
            dbImageDao.updateDbImageFolder(id, folderName)
        }
    }

    suspend fun updateDbImages(ids: List<String>, folderName: String) {
        withContext(Dispatchers.IO) {
            dbImageDao.updateDbImagesFolder(ids, folderName)
        }
    }


    suspend fun deleteDbImage(id: String) {
        withContext(Dispatchers.IO) {
            dbImageDao.deleteDbImage(id)
        }
    }

    suspend fun deleteDbImages(ids: List<String>) {
        withContext(Dispatchers.IO) {
            dbImageDao.deleteDbImages(ids)
        }
    }

    fun getDbImagesFlow() = dbImageDao.getDbImages()

    suspend fun getDbImages() = dbImageDao.getDbImages().firstOrNull().orEmpty()
}