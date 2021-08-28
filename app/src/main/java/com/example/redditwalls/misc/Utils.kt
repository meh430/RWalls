package com.example.redditwalls.misc

import android.Manifest.permission
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.annotation.ColorInt

import android.R

import android.content.res.Resources.Theme

import android.util.TypedValue

import android.view.WindowManager
import com.example.redditwalls.currentWindowMetricsPointCompat
import com.example.redditwalls.models.Resolution
import java.text.NumberFormat
import android.app.DownloadManager
import android.content.pm.PackageManager

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

import androidx.core.content.ContextCompat

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Build.VERSION

import android.os.Build.VERSION.SDK_INT
import android.widget.Toast


object Utils {

    private val MONTHS = listOf(
        "INIT",
        "Jan",
        "Feb",
        "March",
        "April",
        "May",
        "June",
        "July",
        "Aug",
        "Sept",
        "Oct",
        "Nov",
        "Dec"
    )
    private val ORDINALS = listOf("th", "st", "nd", "rd")


    private fun getOrdinal(n: Int): String {
        val suffix = if (n > 0) {
            val index = if (n in 4..20 || n % 10 > 3) {
                0
            } else {
                n % 10
            }

            ORDINALS[index]
        } else {
            ""
        }

        return "$n$suffix"
    }

    fun convertUTC(utc: Long): String {
        val format =
            SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss", Locale.CANADA).format(Date(utc))
        val tempDate = format.trim().split(" at ")
        val date = tempDate[0].trim().split("-")
        val time = tempDate[1].trim().split(":")
        val month = MONTHS[Integer.parseInt(date[0])]
        var hours = Integer.parseInt(time[0])
        val pmam = if (hours > 12) {
            hours -= 12
            "p.m"
        } else {
            "a.m"
        }
        return "$month ${getOrdinal(Integer.parseInt(date[1]))}, ${date[2]} | ${hours}:${time[1]} $pmam"
    }

    fun checkPermission(context: Context): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED
        }
    }

    fun downloadFile(context: Context, imageLink: String) {
        val mgr = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(imageLink)
        val request = DownloadManager.Request(
            downloadUri
        )
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    or DownloadManager.Request.NETWORK_MOBILE
        ).setAllowedOverMetered(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverRoaming(true).setTitle("Downloading $imageLink")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, imageLink)
        mgr.enqueue(request)
    }

    fun getResolution(context: Context): Resolution {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.currentWindowMetricsPointCompat().run {
            Resolution(
                width = x,
                height = y
            )
        }
    }

    fun formatNumber(number: Double, round: Boolean = false) = NumberFormat.getInstance().run {
        val decimalPlaces = if (round) 2 else 0
        minimumFractionDigits = decimalPlaces
        maximumFractionDigits = decimalPlaces
        format(number)
    }

    fun setNavigationBarColor(activity: Activity) {
        val typedValue = TypedValue()
        val theme: Theme = activity.theme
        theme.resolveAttribute(R.attr.colorBackground, typedValue, true)
        @ColorInt val color = typedValue.data
        activity.window.navigationBarColor = color
    }

    fun setFullScreen(window: Window, rootView: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, rootView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.hide(WindowInsetsCompat.Type.captionBar())
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun getDate(): String {
        return SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss", Locale.CANADA).format(Date());
    }

    fun saveBitmap(bitmap: Bitmap, name: String?, context: Context): String {
        val fName = name ?: UUID.randomUUID().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val relativeLocation: String =
                Environment.DIRECTORY_PICTURES + File.separator + "RedditWalls"

            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)

            val resolver = context.contentResolver

            var stream: OutputStream? = null
            var uri: Uri? = null

            try {
                val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                uri = resolver.insert(contentUri, contentValues)
                if (uri == null) {
                    throw IOException("Failed to create new MediaStore record.")
                }
                stream = resolver.openOutputStream(uri)
                if (stream == null) {
                    throw IOException("Failed to get output stream.")
                }
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Failed to save bitmap.")
                }

                Toast.makeText(context, "Downloaded image", Toast.LENGTH_SHORT).show()

            } catch (e: IOException) {
                if (uri != null) {
                    resolver.delete(uri, null, null)
                }
                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
            } finally {
                stream?.close()
            }
        } else {
            val root = Environment.getExternalStorageDirectory().toString()
            val myDir = File("$root/RedditWalls")
            myDir.mkdirs()
            val file = File(myDir, fName)
            if (file.exists())
                file.delete()
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    file.absolutePath,
                    file.name,
                    file.name
                )

                Toast.makeText(context, "Downloaded image", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
            }
        }

        return fName
    }
}