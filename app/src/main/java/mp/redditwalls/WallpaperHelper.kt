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
import mp.redditwalls.models.toImageItemItemUiState
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
        (getRandomFavoriteImage(Unit) as? DomainResult.Success)?.data?.let { (home, lock) ->
            if (home != null) {
                setImageAsWallpaper(
                    context,
                    home.domainImageUrls[0].url,
                    WallpaperLocation.HOME,
                    home.toImageItemItemUiState().toDomainWallpaperRecentActivityItem(
                        location = mp.redditwalls.local.enums.WallpaperLocation.HOME,
                        refresh = true
                    )
                )
            }
            if (lock != null) {
                setImageAsWallpaper(
                    context,
                    lock.domainImageUrls[0].url,
                    WallpaperLocation.LOCK,
                    lock.toImageItemItemUiState().toDomainWallpaperRecentActivityItem(
                        location = mp.redditwalls.local.enums.WallpaperLocation.LOCK_SCREEN,
                        refresh = true
                    )
                )
            }
        }
    }

    suspend fun setImageAsWallpaper(
        context: Context,
        imageUrl: String,
        location: WallpaperLocation,
        recentActivityItem: DomainRecentActivityItem? = null
    ) {
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
    HOME(0, "Home"),
    LOCK(1, "Lock Screen"),
    BOTH(2, "Home and Lock Screen");

    companion object {
        fun fromId(id: Int) = values().fromId(id, BOTH)
    }
}