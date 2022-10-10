package mp.redditwalls.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal object Prefs {
    private val Context.preferences by preferencesDataStore(name = "preferences")

    fun getDataStore(context: Context) = context.preferences
}