package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalImageFoldersRepository

class DeleteFolderUseCase @Inject constructor(
    private val localImageFoldersRepository: LocalImageFoldersRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(params: String) {
        localImageFoldersRepository.deleteDbImageFolderAndDbImages(params)
    }
}