package mp.redditwalls

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mp.redditwalls.preferences.PreferencesRepository
import mp.redditwalls.utils.RwToLocalMigration
import timber.log.Timber

@HiltAndroidApp
class RWApplication : Application(), Configuration.Provider {
    private var applicationScope = MainScope()

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var rwToLocalMigration: RwToLocalMigration

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationScope.launch {
            AppCompatDelegate.setDefaultNightMode(
                preferencesRepository.getTheme().first().toThemeMode()
            )
            rwToLocalMigration.migrate()
        }

        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel()
        applicationScope = MainScope()
    }
}