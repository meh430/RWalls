package mp.redditwalls.misc

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import mp.redditwalls.repositories.SettingsRepository

class Toaster @Inject constructor(
    @ApplicationContext val context: Context,
    private val settingsRepository: SettingsRepository
) {
    fun t(message: String, force: Boolean = false) {
        if (force || settingsRepository.toastEnabled()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}