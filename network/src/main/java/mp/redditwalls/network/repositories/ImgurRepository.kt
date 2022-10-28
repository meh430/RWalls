package mp.redditwalls.network.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.network.services.ImgurService

class ImgurRepository @Inject constructor(private val imgurService: ImgurService) {
    suspend fun getAlbum(id: String) = withContext(Dispatchers.IO) {
        imgurService.getAlbumImages(id).data
    }
}