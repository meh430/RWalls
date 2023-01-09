package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.SettingsResult
import mp.redditwalls.local.repositories.LocalImageFoldersRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetSettingsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val localImageFoldersRepository: LocalImageFoldersRepository
) : FlowUseCase<SettingsResult, Unit>(SettingsResult()) {
    override fun execute() = combine(
        preferencesRepository.getAllPreferences(),
        localImageFoldersRepository.getDbImageFolderNames()
    ) { preferences, folderNames ->
        SettingsResult(
            preferencesData = preferences,
            folderNames = folderNames
        )
    }
}