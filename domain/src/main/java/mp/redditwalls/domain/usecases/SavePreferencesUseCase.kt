package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.preferences.PreferencesData
import mp.redditwalls.preferences.PreferencesRepository

class SavePreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
): UseCase<PreferencesData, Unit>() {
    override suspend fun execute(params: PreferencesData) {
        preferencesRepository.setAllPreferences(params)
    }
}