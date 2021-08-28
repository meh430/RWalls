package com.example.redditwalls

import android.graphics.Insets
import android.graphics.Point
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
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