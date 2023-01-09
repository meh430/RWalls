package mp.redditwalls.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.activities.MainActivity
import mp.redditwalls.models.Image
import mp.redditwalls.models.Resolution


object Utils {

    private val MONTHS = listOf(
        "INIT",
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
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

    fun getResolution(context: Context): Resolution {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.currentWindowMetricsPointCompat().run {
            Resolution(
                width = x,
                height = y
            )
        }
    }

    fun formatNumber(number: Double, round: Boolean = false): String =
        NumberFormat.getInstance().run {
            val decimalPlaces = if (round) 2 else 0
            minimumFractionDigits = decimalPlaces
            maximumFractionDigits = decimalPlaces
            format(number)
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

    fun getImageLoadingDrawable(context: Context) = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        setColorSchemeColors(Color.RED)
        start()
    }

    fun getDate(): String {
        return SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss", Locale.CANADA).format(Date())
    }

    fun getGlideRequestOptions() = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .centerCrop()
        .dontAnimate()
        .dontTransform()

    @Suppress("Deprecation")
    fun saveBitmap(
        bitmap: Bitmap,
        name: String? = null,
        context: Context,
        showToasts: Boolean = true
    ): String {
        val fName = name ?: UUID.randomUUID().toString()
        if (SDK_INT >= Build.VERSION_CODES.Q) {
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

                if (showToasts) {
                    Toast.makeText(context, "Downloaded image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                if (uri != null) {
                    resolver.delete(uri, null, null)
                }
                if (showToasts) {
                    Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
                }
            } finally {
                stream?.close()
            }
        } else {
            val root = Environment.getExternalStorageDirectory().toString()
            val myDir = File("$root/RedditWalls")
            myDir.mkdirs()
            val file = File(myDir, "$fName.jpg")
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

                if (showToasts) {
                    Toast.makeText(context, "Downloaded image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (showToasts) {
                    Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return fName
    }

    suspend fun downloadAllImages(
        context: Context,
        imageLoader: ImageLoader,
        images: List<Image>
    ) {
        val notify = ProgressNotification(context, images.size)
        notify.sendNotification()

        val resolution = getResolution(context)

        withContext(Dispatchers.Default) {
            images.forEachIndexed { index, image ->
                val bitmap = imageLoader.loadImage(context, image.imageLink, resolution)
                saveBitmap(bitmap = bitmap, context = context, showToasts = false)

                withContext(Dispatchers.Main) {
                    notify.updateProgress(index + 1)
                }
            }
        }

        notify.finish()
    }

    fun getStatusBarHeight(context: Context): Int =
        context.run {
            val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(statusBarHeightId)
        }

    fun triggerRebirth(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
        Runtime.getRuntime().exit(0)
    }

    fun getFormattedDate(date: Date): String {
        val dateString =
            SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss", Locale.getDefault()).format(date)
        val tempDate = dateString.trim().split(" at ")
        val dateArr = tempDate[0].trim().split("-")
        val timeArr = tempDate[1].trim().split(":")
        val day = dateArr[1]
        val year = dateArr[2].slice(2..3)
        val month = MONTHS[Integer.parseInt(dateArr[0])]
        var hours = Integer.parseInt(timeArr[0])
        val minutes = Integer.parseInt(timeArr[1])
        val pmam = if (hours > 12) {
            hours -= 12
            "PM"
        } else {
            "AM"
        }

        return "$hours:$minutes $pmam · $day $month $year"
    }
}