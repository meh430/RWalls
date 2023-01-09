package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.ImageId
import mp.redditwalls.local.repositories.LocalImagesRepository

class UpdateFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<UpdateFavoriteImageUseCase.Params, Unit>() {

    override suspend fun execute(params: Params) {
        if (params.ids.size == 1) {
            localImagesRepository.updateDbImageFolder(
                id = params.ids[0].dbImageId,
                folderName = params.folderName
            )
        } else if (params.ids.isNotEmpty()) {
            localImagesRepository.updateDbImages(
                ids = params.ids.map { it.dbImageId },
                folderName = params.folderName
            )
        }
    }

    data class Params(
        val ids: List<ImageId>,
        val folderName: String
    )
}