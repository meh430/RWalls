package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.repositories.LocalImageFoldersRepository

class UpdateFolderSettingsUseCase @Inject constructor(
    private val localImageFoldersRepository: LocalImageFoldersRepository
) : UseCase<UpdateFolderSettingsUseCase.Params, Unit>() {
    override suspend fun execute(params: Params) {
        params.run {
            localImageFoldersRepository.updateDbImageFolderSettings(
                name = folderName,
                refreshEnabled = refreshEnabled,
                refreshLocation = refreshLocation
            )
        }
    }

    data class Params(
        val folderName: String,
        val refreshEnabled: Boolean,
        val refreshLocation: WallpaperLocation
    )
}