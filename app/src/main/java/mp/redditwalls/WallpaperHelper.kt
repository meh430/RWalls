package mp.redditwalls

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.usecases.AddRecentActivityItemUseCase
import mp.redditwalls.domain.usecases.GetRandomFavoriteImage
import mp.redditwalls.models.toDomainWallpaperRecentActivityItem
import mp.redditwalls.models.toImageItemUiState
import mp.redditwalls.repositories.SettingsItem
import mp.redditwalls.utils.ImageLoader
import mp.redditwalls.utils.Utils
import mp.redditwalls.utils.fromId

class WallpaperHelper @Inject constructor(
    private val imageLoader: ImageLoader,
    private val getRandomFavoriteImage: GetRandomFavoriteImage,
    private val addRecentActivityItemUseCase: AddRecentActivityItemUseCase
) {

    suspend fun refreshWallpaper(context: Context) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "Refreshing wallpaper",
                Toast.LENGTH_SHORT
            ).show()
        }
        (getRandomFavoriteImage(Unit) as? DomainResult.Success)?.data?.let { (home, lock) ->
            if (home != null) {
                setImageAsWallpaper(
                    context,
                    home.imageUrls[0].url,
                    WallpaperLocation.HOME,
                    home.toImageItemUiState().toDomainWallpaperRecentActivityItem(
                        location = mp.redditwalls.local.enums.WallpaperLocation.HOME,
                        refresh = true
                    ),
                    true
                )
            }
            if (lock != null) {
                setImageAsWallpaper(
                    context,
                    lock.imageUrls[0].url,
                    WallpaperLocation.LOCK,
                    lock.toImageItemUiState().toDomainWallpaperRecentActivityItem(
                        location = mp.redditwalls.local.enums.WallpaperLocation.LOCK_SCREEN,
                        refresh = true
                    ),
                    true
                )
            }
        }
    }

    suspend fun setImageAsWallpaper(
        context: Context,
        imageUrl: String,
        location: WallpaperLocation,
        recentActivityItem: DomainRecentActivityItem? = null,
        refresh: Boolean = false
    ) {
        if (!refresh) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Setting wallpaper on ${location.displayText}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        try {
            val resolution = Utils.getResolution(context)
            val wallpaper = imageLoader.loadImage(
                context,
                imageUrl,
                resolution
            )

            withContext(Dispatchers.Main) {
                setBitmapAsWallpaper(context, wallpaper, location)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        } finally {
            recentActivityItem?.let {
                addRecentActivityItemUseCase(it)
            }
        }
    }

    fun setBitmapAsWallpaper(
        context: Context,
        bitmap: Bitmap,
        location: WallpaperLocation
    ): Boolean {
        return try {
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

            true
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun showLocationPickerDialog(context: Context, onChoose: (WallpaperLocation) -> Unit) {
        val items = WallpaperLocation.values().map { it.displayText }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle("Set where?")
            .setItems(items) { _, i ->
                onChoose(WallpaperLocation.fromId(i))
            }.show()
    }
}

enum class WallpaperLocation(override val id: Int, override val displayText: String) :
    SettingsItem {
    HOME(0, "Home Screen"),
    LOCK(1, "Lock Screen"),
    BOTH(2, "Home and Lock Screen");

    companion object {
        fun fromId(id: Int) = values().fromId(id, BOTH)
    }
}