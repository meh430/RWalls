package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.FavoriteImagesResult
import mp.redditwalls.domain.models.toDomainImageFolder
import mp.redditwalls.local.repositories.LocalImageFoldersRepository
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetFavoriteImagesUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository,
    private val localImageFoldersRepository: LocalImageFoldersRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<FavoriteImagesResult, GetFavoriteImagesUseCase.Params>(FavoriteImagesResult()) {
    override fun execute() = combine(
        paramsFlow,
        localImagesRepository.getDbImagesFlow(),
        localImageFoldersRepository.getDbImageFolderNames(),
        preferencesRepository.getAllPreferences()
    ) { params, _, imageFolderNames, preferences ->
        val folder = localImageFoldersRepository.getDbImageFolderWithImages(params.imageFolderName)
        data.copy(
            folderNames = imageFolderNames,
            imageFolder = folder.toDomainImageFolder(preferences.previewResolution),
            masterRefreshEnabled = preferences.refreshEnabled
        )
    }

    data class Params(
        val imageFolderName: String = ""
    )
}