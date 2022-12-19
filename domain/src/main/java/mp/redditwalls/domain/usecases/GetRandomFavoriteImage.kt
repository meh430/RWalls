package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.local.repositories.LocalImageFoldersRepository
import mp.redditwalls.preferences.enums.ImageQuality

class GetRandomFavoriteImage @Inject constructor(
    private val localImageFoldersRepository: LocalImageFoldersRepository
) : UseCase<Unit, GetRandomFavoriteImage.GetRandomFavoriteImageResult>() {
    override suspend fun execute(params: Unit): GetRandomFavoriteImageResult {
        return GetRandomFavoriteImageResult(
            homeScreenImage = localImageFoldersRepository.getRandomHomeScreenDbImage()
                ?.toDomainImage(ImageQuality.HIGH, true),
            lockScreenImage = localImageFoldersRepository.getRandomLockScreenDbImage()
                ?.toDomainImage(ImageQuality.HIGH, true)
        )
    }

    data class GetRandomFavoriteImageResult(
        val homeScreenImage: DomainImage?,
        val lockScreenImage: DomainImage?
    )
}