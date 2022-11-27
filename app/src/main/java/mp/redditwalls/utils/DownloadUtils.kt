package mp.redditwalls.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DownloadUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun downloadImage(imageUrl: String) {
        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("RWalls image download")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
        val downloadManager = getSystemService(context, DownloadManager::class.java)!!
        downloadManager.enqueue(request)
    }

    fun downloadImages(imageUrls: List<String>) {
        imageUrls.forEach { downloadImage(it) }
    }
}