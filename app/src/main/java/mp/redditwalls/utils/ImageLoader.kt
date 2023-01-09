package mp.redditwalls.utils

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.models.Resolution

// get() is called in an IO coroutine
@Suppress("BlockingMethodInNonBlockingContext")
class ImageLoader @Inject constructor() {

    // Use to save a bitmap
    suspend fun loadImage(context: Context, image: String, resolution: Resolution): Bitmap =
        withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(image)
                .override(resolution.width, resolution.height)
                .submit().get()
        }

    suspend fun getImageSize(context: Context, image: String, resolution: Resolution) =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = loadImage(context, image, resolution)
                val tempFile = File(context.cacheDir, "temp")
                val out = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                val sizeInMB = tempFile.length() / (1024.0 * 1024.0)

                out.flush()
                out.close()
                tempFile.delete()

                sizeInMB
            } catch (e: Exception) {
                0.0
            }
        }
}