package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalImagesRepository

class RemoveFavoriteImagesUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<List<String>, Unit>() {
    override suspend fun execute(params: List<String>) {
        localImagesRepository.deleteDbImages(ids = params)
    }
}