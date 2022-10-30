package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.Utils
import mp.redditwalls.domain.models.PreferencesResult
import mp.redditwalls.preferences.PreferencesRepository

class GetPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<PreferencesResult, Unit>(PreferencesResult()) {
    override fun execute(params: Unit) = preferencesRepository.run {
        Utils.combine(
            getDefaultHomeSort(),
            getPreviewResolution(),
            getTheme(),
            getRefreshEnabled(),
            getRefreshInterval(),
            getDataSetting(),
            getVerticalSwipeFeedEnabled(),
            getAllowNsfw()
        ) { defaultHomeSort,
            previewResolution,
            theme,
            refreshEnabled,
            refreshInterval,
            dataSetting,
            verticalSwipeFeedEnabled,
            allowNsfw ->
            PreferencesResult(
                defaultHomeSort = defaultHomeSort,
                previewResolution = previewResolution,
                theme = theme,
                refreshEnabled = refreshEnabled,
                refreshInterval = refreshInterval,
                dataSetting = dataSetting,
                verticalSwipeFeedEnabled = verticalSwipeFeedEnabled,
                allowNsfw = allowNsfw
            )
        }
    }
}