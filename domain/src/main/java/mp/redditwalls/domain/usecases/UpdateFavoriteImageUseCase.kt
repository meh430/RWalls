package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.repositories.LocalImagesRepository

class UpdateFavoriteImageUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository
) : UseCase<UpdateFavoriteImageUseCase.Params, Unit>() {

    override suspend fun execute(params: Params) {
        if (params.ids.size == 1) {
            localImagesRepository.updateDbImage(
                id = params.ids[0],
                refreshLocation = params.refreshLocation
            )
        } else if (params.ids.isNotEmpty()) {
            localImagesRepository.updateDbImages(
                ids = params.ids,
                refreshLocation = params.refreshLocation
            )
        }
    }

    data class Params(
        val ids: List<String>,
        val refreshLocation: WallpaperLocation
    )
}