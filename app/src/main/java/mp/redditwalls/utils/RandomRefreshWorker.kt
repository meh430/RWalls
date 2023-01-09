package mp.redditwalls.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mp.redditwalls.WallpaperHelper

@HiltWorker
class RandomRefreshWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    val wallpaperHelper: WallpaperHelper
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        wallpaperHelper.refreshWallpaper(context)
        return Result.success()
    }
}