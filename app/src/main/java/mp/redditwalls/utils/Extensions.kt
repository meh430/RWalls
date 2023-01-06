package mp.redditwalls.utils

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.TypedValue
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlinx.coroutines.flow.filter
import mp.redditwalls.RefreshWallpaper
import mp.redditwalls.activities.MainActivity
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

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyGridState.OnBottomReached(
    buffer: Int = 0,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }.filter { it }.collect {
            onLoadMore()
        }
    }
}

fun Context.onWriteStoragePermissionGranted(isGranted: Boolean) {
    val text = if (isGranted) {
        "Permission granted, please try to download once more"
    } else {
        "Write storage permission required to download"
    }
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.writePermissionGranted() = SDK_INT >= Build.VERSION_CODES.Q ||
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

fun Context.requestToPinWidget() {
    if (SDK_INT >= Build.VERSION_CODES.O) {
        val appWidgetManager: AppWidgetManager? = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, RefreshWallpaper::class.java)
        if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
            val pinnedWidgetCallbackIntent = Intent(this, MainActivity::class.java)
            val successCallback = PendingIntent.getBroadcast(
                this,
                0,
                pinnedWidgetCallbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        }
    }
}