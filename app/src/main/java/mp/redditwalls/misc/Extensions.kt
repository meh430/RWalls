package mp.redditwalls.misc

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Point
import android.net.Uri
import android.util.TypedValue
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
import mp.redditwalls.repositories.SettingsItem
import org.json.JSONArray
import org.json.JSONObject


inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    val length = length()
    for (i in 0 until length) {
        action(getJSONObject(i))
    }
}

fun <T> MutableLiveData<T>.notifyObservers() {
    this.value = this.value
}

fun WindowManager.currentWindowMetricsPointCompat(): Point {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowInsets = currentWindowMetrics.windowInsets
        var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        windowInsets.displayCutout?.run {
            insets = Insets.max(
                insets,
                Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
            )
        }
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        Point(
            currentWindowMetrics.bounds.width() - insetsWidth,
            currentWindowMetrics.bounds.height() - insetsHeight
        )
    } else {
        Point().apply {
            @Suppress("Deprecation")
            defaultDisplay.getRealSize(this)
        }
    }
}

fun String.launchBrowser(activity: Activity) {
    Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(this@launchBrowser)
    }.also { activity.startActivity(it) }
}

fun String.removeSubPrefix(): String =
    if (startsWith("r/") && length > 2) {
        substring(2)
    } else {
        this
    }

fun <T : SettingsItem> Array<T>.fromId(id: Int, default: T): T {
    return find { it.id == id } ?: default
}

fun <T : SettingsItem> Array<T>.fromDisplayText(text: String, default: T): T {
    return find { it.displayText == text } ?: default
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

fun <T> SharedPreferences.putValue(key: String, value: T) {
    edit().apply {
        when (value) {
            is Boolean -> {
                putBoolean(key, value)
            }
            is String -> {
                putString(key, value)
            }
            is Int -> {
                putInt(key, value)
            }
        }

        apply()
    }
}