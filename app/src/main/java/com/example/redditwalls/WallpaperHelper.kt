package com.example.redditwalls

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.misc.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class WallpaperHelper @Inject constructor(private val imageLoader: ImageLoader) {
    suspend fun setImageLinkAsWallpaper(
        context: Context,
        imageLink: String,
        location: WallpaperLocation
    ) {
        try {
            val wallpaper = imageLoader.loadImage(
                context,
                imageLink,
                Utils.getResolution(context)
            )
            setBitmapAsWallpaper(context, wallpaper, location)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun setBitmapAsWallpaper(context: Context, bitmap: Bitmap, location: WallpaperLocation) {
        try {
            val wm = context.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                val which = when (location) {
                    WallpaperLocation.HOME -> WallpaperManager.FLAG_SYSTEM
                    WallpaperLocation.LOCK -> WallpaperManager.FLAG_LOCK
                    WallpaperLocation.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wm.setBitmap(bitmap, null, true, which)
            } else {
                wm.setBitmap(bitmap)
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showLocationPickerDialog(context: Context, onChoose: (WallpaperLocation) -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Set where?")
            .setItems(R.array.location_options) { _, i ->
                onChoose(WallpaperLocation.fromId(i))
                Toast.makeText(context, "Successfully set wallpaper", Toast.LENGTH_SHORT).show()
            }.show()
    }
}

enum class WallpaperLocation(val id: Int) {
    HOME(0),
    LOCK(1),
    BOTH(2);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id } ?: BOTH
    }
}