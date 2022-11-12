package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalImagesRepository

class RemoveFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(params: String) {
        localImagesRepository.deleteDbImage(id = params)
    }
}