package com.example.redditwalls

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 */
@AndroidEntryPoint
class RefreshWallpaper : AppWidgetProvider() {
    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    private val job = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        job.cancel()
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        coroutineScope.launch {
            wallpaperHelper.setRandomFavoriteWallpaper(context)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.refresh_wallpaper)

    val intent = Intent(context, RefreshWallpaper::class.java)
    intent.action = "CHANGE_TIME"
    val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    val pending = PendingIntent.getBroadcast(context, 3, intent, flags)

    views.setOnClickPendingIntent(R.id.refresh_wall, pending)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}