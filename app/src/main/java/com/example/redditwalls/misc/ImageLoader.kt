package com.example.redditwalls.misc

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.example.redditwalls.models.Resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
            loadImage(context, image, resolution).allocationByteCount / (1024.0 * 1024.0)
        }
}