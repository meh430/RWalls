package com.example.redditwalls.misc

import android.content.Context
import com.bumptech.glide.Glide
import com.example.redditwalls.repositories.Resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageLoader @Inject constructor() {

    // Use to save a bitmap
    suspend fun loadImage(context: Context, image: String, resolution: Resolution) =
        withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(image)
                .override(resolution.width, resolution.height)
                .submit().get()
        }

    suspend fun getImageSize(context: Context, image: String, resolution: Resolution) =
        withContext(Dispatchers.IO) {
            val image = loadImage(context, image, resolution)
            image.allocationByteCount / (1024.0 * 1024.0)
        }
}