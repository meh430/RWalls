package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.first
import mp.redditwalls.preferences.PreferencesData
import mp.redditwalls.preferences.PreferencesRepository

class GetPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : UseCase<Unit, PreferencesData>() {
    override suspend fun execute(params: Unit) = preferencesRepository.getAllPreferences().first()
}