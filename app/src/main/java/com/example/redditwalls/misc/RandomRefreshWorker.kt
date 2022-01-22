package com.example.redditwalls.misc

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.redditwalls.WallpaperHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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