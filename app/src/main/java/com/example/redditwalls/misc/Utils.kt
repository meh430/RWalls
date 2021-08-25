package com.example.redditwalls.misc

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

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

    fun getDate(): String {
        return SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss", Locale.CANADA).format(Date());
    }

    fun saveBitmap(bitmap: Bitmap, context: Context): String {
        val fName = (0..999999999).random().toString().replace(" ", "") + ".jpg"
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

            } catch (e: IOException) {
                if (uri != null) {
                    resolver.delete(uri, null, null)
                }
                throw e
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return fName
    }
}