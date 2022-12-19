package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalImagesRepository

class UpdateFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<UpdateFavoriteImageUseCase.Params, Unit>() {

    override suspend fun execute(params: Params) {
        if (params.ids.size == 1) {
            localImagesRepository.updateDbImageFolder(
                id = params.ids[0],
                folderName = params.folderName
            )
        } else if (params.ids.isNotEmpty()) {
            localImagesRepository.updateDbImages(
                ids = params.ids,
                folderName = params.folderName
            )
        }
    }

    data class Params(
        val ids: List<String>,
        val folderName: String
    )
}