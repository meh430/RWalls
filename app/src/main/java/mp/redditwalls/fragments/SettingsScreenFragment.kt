package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.design.RwTheme
import mp.redditwalls.preferences.enums.DataSetting
import mp.redditwalls.preferences.enums.RefreshInterval
import mp.redditwalls.ui.screens.SettingsScreen
import mp.redditwalls.utils.RandomRefreshWorker
import mp.redditwalls.viewmodels.SettingsScreenViewModel

@AndroidEntryPoint
class SettingsScreenFragment : Fragment() {

    private val vm: SettingsScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RwTheme {
                SettingsScreen(
                    vm = vm,
                    navController = findNavController()
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.savePreferences()
        vm.uiState.run {
            if (refreshEnabled.value && refreshSettingsChanged.value) {
                startRandomRefreshWork(
                    interval = refreshInterval.value,
                    dataSetting = dataSetting.value
                )
            } else {
                cancelRandomRefreshWork()
            }
        }
    }

    private fun startRandomRefreshWork(interval: RefreshInterval, dataSetting: DataSetting) {
        val networkType = when (dataSetting) {
            DataSetting.WIFI -> NetworkType.UNMETERED
            DataSetting.DATA -> NetworkType.METERED
            DataSetting.BOTH -> NetworkType.CONNECTED
        }
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(networkType)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val work = PeriodicWorkRequestBuilder<RandomRefreshWorker>(
            interval.getAmount(),
            interval.getTimeUnit()
        ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.enqueueUniquePeriodicWork(
            SettingsFragment.RANDOM_REFRESH_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
    }

    private fun cancelRandomRefreshWork() {
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.cancelUniqueWork(SettingsFragment.RANDOM_REFRESH_WORK)
    }

}
