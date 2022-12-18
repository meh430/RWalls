package mp.redditwalls.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Point
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.TypedValue
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import mp.redditwalls.design.components.IconText
import mp.redditwalls.preferences.enums.SortOrder
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

fun String.launchBrowser(context: Context) {
    Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(this@launchBrowser)
    }.also { context.startActivity(it) }
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

fun Int.toFriendlyCount(): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
    hierarchy.any { it.id == destId }

// Move to separate file?
@Composable
fun rememberSortMenuOptions(context: Context) = remember {
    SortOrder.values().map { IconText(text = context.getString(it.stringId)) }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible =
        androidx.compose.foundation.layout.WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}