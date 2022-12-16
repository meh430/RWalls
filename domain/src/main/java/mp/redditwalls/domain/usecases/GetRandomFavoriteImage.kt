package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.preferences.enums.ImageQuality

class GetRandomFavoriteImage @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<Unit, GetRandomFavoriteImage.GetRandomFavoriteImageResult>() {
    override suspend fun execute(params: Unit): GetRandomFavoriteImageResult {
        return GetRandomFavoriteImageResult(
            homeScreenImage = localImagesRepository.getRandomHomeScreenDbImage()
                .map { it.toDomainImage(ImageQuality.HIGH, true) }.firstOrNull(),
            lockScreenImage = localImagesRepository.getRandomLockScreenDbImage()
                .map { it.toDomainImage(ImageQuality.HIGH, true) }.firstOrNull()
        )
    }

    data class GetRandomFavoriteImageResult(
        val homeScreenImage: DomainImage?,
        val lockScreenImage: DomainImage?
    )
}