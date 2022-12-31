package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalImagesRepository

class GetFavoriteImageIdsSetUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<Unit, Set<String>>() {
    override suspend fun execute(params: Unit) =
        localImagesRepository.getDbImages().map { it.id }.toSet()
}