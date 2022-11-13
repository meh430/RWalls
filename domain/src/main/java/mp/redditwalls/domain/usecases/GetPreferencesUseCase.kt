package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.preferences.PreferencesData
import mp.redditwalls.preferences.PreferencesRepository

class GetPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<PreferencesData, Unit>(PreferencesData()) {
    override fun execute() = preferencesRepository.getAllPreferences()
}