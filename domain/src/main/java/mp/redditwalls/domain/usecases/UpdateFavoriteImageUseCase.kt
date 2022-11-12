package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.repositories.LocalImagesRepository

class UpdateFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<UpdateFavoriteImageUseCase.Params, Unit>() {
    override suspend fun execute(params: Params) {
        localImagesRepository.updateDbImage(
            id = params.id,
            refreshLocation = params.refreshLocation
        )
    }

    data class Params(
        val id: String,
        val refreshLocation: WallpaperLocation
    )
}