package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.ImageId
import mp.redditwalls.local.repositories.LocalImagesRepository

class RemoveFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<ImageId, Unit>() {
    override suspend fun execute(params: ImageId) {
        localImagesRepository.deleteDbImage(id = params.dbImageId)
    }
}