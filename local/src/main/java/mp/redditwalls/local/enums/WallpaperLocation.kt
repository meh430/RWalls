package mp.redditwalls.local.enums

import android.content.Context
import mp.redditwalls.local.R

enum class WallpaperLocation {
    HOME,
    LOCK_SCREEN,
    BOTH
}

fun WallpaperLocation.getString(context: Context) = when (this) {
    WallpaperLocation.HOME -> context.getString(R.string.home_screen)
    WallpaperLocation.LOCK_SCREEN -> context.getString(R.string.lock_screen)
    WallpaperLocation.BOTH -> context.getString(R.string.both_screens)
}