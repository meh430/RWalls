package mp.redditwalls.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import mp.redditwalls.R

class ProgressNotification(val context: Context, private val size: Int) {
    private lateinit var notifyManager: NotificationManager
    private val notificationBuilder: NotificationCompat.Builder
        get() {
            val notifyBuilder = NotificationCompat.Builder(context, SECONDARY_CHANNEL_ID)
            notifyBuilder.apply {
                setOngoing(false)
                setOnlyAlertOnce(true)
                setAutoCancel(true)
                setContentTitle("Downloading all images")
                setContentText("Please wait...")
                setProgress(size, 0, false)
                setSmallIcon(R.drawable.ic_download)
                priority = NotificationCompat.PRIORITY_HIGH
                setDefaults(NotificationCompat.DEFAULT_ALL)
            }
            return notifyBuilder
        }

    init {
        createNotificationChannel()
    }

    fun sendNotification() {
        val notifyBuilder = notificationBuilder
        //send notification through the channel in the manager
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    fun updateProgress(progress: Int) {
        val notifyBuilder = notificationBuilder
        notifyBuilder.setProgress(size, progress, false)
        notifyBuilder.setContentText("Downloaded $progress / $size")
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    fun finish() {
        notifyManager.cancel(NOTIFICATION_ID)
        val viewIntent = Intent()
        viewIntent.action = Intent.ACTION_VIEW
        viewIntent.type = "image/*"
        viewIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingFlags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        val viewPending = PendingIntent.getActivity(
            context, NOTIFICATION_ID,
            viewIntent, pendingFlags
        )
        val notifyBuilder = notificationBuilder
        notifyBuilder.apply {
            setContentIntent(viewPending)
            setProgress(0, 0, false)
            setContentText("Finished downloading")
            setOngoing(false)
            setAutoCancel(true)
        }
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    private fun createNotificationChannel() {
        notifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(
                SECONDARY_CHANNEL_ID,
                "Download Notification", NotificationManager.IMPORTANCE_HIGH
            )

            //configuring notification settings when sent
            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
                description = "Notification for Download Progress"
            }
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val SECONDARY_CHANNEL_ID = "secondary_notification_channel"
    }
}