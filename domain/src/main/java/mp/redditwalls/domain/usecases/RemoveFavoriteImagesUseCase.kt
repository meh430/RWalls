package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.ImageId
import mp.redditwalls.local.repositories.LocalImagesRepository

class RemoveFavoriteImagesUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<List<ImageId>, Unit>() {
    override suspend fun execute(params: List<ImageId>) {
        localImagesRepository.deleteDbImages(ids = params.map { it.dbImageId })
    }
}