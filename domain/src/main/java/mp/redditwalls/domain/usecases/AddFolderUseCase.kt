package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.models.DbImageFolder
import mp.redditwalls.local.repositories.LocalImageFoldersRepository

class AddFolderUseCase @Inject constructor(
    private val localImageFoldersRepository: LocalImageFoldersRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(params: String) {
        localImageFoldersRepository.insertDbImageFolder(
            dbImageFolder = DbImageFolder(
                name = params,
                refreshEnabled = true
            )
        )
    }
}