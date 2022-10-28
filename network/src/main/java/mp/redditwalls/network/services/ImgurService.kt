package mp.redditwalls.network.services

import mp.redditwalls.network.models.ImgurAlbumResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ImgurService {
    @GET("/3/album/{id}")
    fun getAlbumImages(@Path("id") id: String): ImgurAlbumResponse
}