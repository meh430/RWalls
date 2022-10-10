package mp.redditwalls

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.misc.ImageLoader
import mp.redditwalls.misc.Toaster
import mp.redditwalls.misc.Utils
import mp.redditwalls.misc.fromId
import mp.redditwalls.models.History
import mp.redditwalls.models.Image
import mp.redditwalls.repositories.FavoriteImagesRepository
import mp.redditwalls.repositories.HistoryRepository
import mp.redditwalls.repositories.SettingsItem
import mp.redditwalls.repositories.SettingsRepository

class WallpaperHelper @Inject constructor(
    private val imageLoader: ImageLoader,
    private val favoriteImagesRepository: FavoriteImagesRepository,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val toaster: Toaster
) {

    suspend fun refreshWallpaper(context: Context) {
        val numFavs = favoriteImagesRepository.getFavoriteImagesCount()
        val randomOrder = settingsRepository.randomOrder()
        var image: Image? = null
        if (numFavs == 0) {
            toast("No favorites to choose from :(")
            return
        } else if (numFavs == 1 || randomOrder) {
            image = favoriteImagesRepository.getRandomFavoriteImage()
        } else if (!randomOrder) {
            val index = settingsRepository.getRefreshIndex()
            image = getNextWallpaper(index)
        }

        toast("Setting wallpaper...")
        image?.let {
            setImageAsWallpaper(
                context,
                it,
                settingsRepository.getRandomRefreshLocation(),
                true
            )
        }
    }

    private suspend fun getNextWallpaper(index: Int): Image {
        val image = favoriteImagesRepository.getFavoriteImage(index)
        return image?.also {
            settingsRepository.setRefreshIndex(index + 1)
        } ?: getNextWallpaper(0)
    }

    suspend fun setRandomFavoriteWallpaper(context: Context) {
        toast("Setting wallpaper...")
        val image = favoriteImagesRepository.getRandomFavoriteImage()

        image?.let {
            setImageAsWallpaper(
                context,
                it,
                settingsRepository.getRandomRefreshLocation(),
                true
            )
        }
        if (image == null) {
            toast("No favorites to choose from :(")
        }
    }

    suspend fun setImageAsWallpaper(
        context: Context,
        image: Image,
        location: WallpaperLocation,
        refresh: Boolean = false
    ) {
        try {
            toast("Setting wallpaper...")
            val resolution = Utils.getResolution(context)
            val wallpaper = imageLoader.loadImage(
                context,
                image.imageLink,
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
            historyRepository.insertHistory(
                History(
                    image = image,
                    dateCreated = Date().time,
                    manuallySet = !refresh,
                    location = location.id
                )
            )
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

            toaster.t("Successfully set wallpaper")
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

    private suspend fun toast(msg: String) {
        withContext(Dispatchers.Main) {
            toaster.t(msg)
        }
    }
}

enum class WallpaperLocation(override val id: Int, override val displayText: String) :
    SettingsItem {
    HOME(0, "Home"),
    LOCK(1, "Lock Screen"),
    BOTH(2, "Home and Lock Screen");

    companion object {
        fun fromId(id: Int) = values().fromId(id, BOTH)
    }
}