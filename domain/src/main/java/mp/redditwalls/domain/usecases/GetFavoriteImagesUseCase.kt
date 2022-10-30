package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetFavoriteImagesUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<FeedResult, GetFavoriteImagesUseCase.Params>(FeedResult()) {
    override suspend operator fun invoke(params: Params) = withContext(Dispatchers.IO) {
        combine(
            localImagesRepository.getDbImagesFlow(),
            preferencesRepository.getPreviewResolution()
        ) { dbImages, previewResolution ->
            val domainImages = dbImages.filter { dbImage ->
                params.wallpaperLocation?.let { it.name == dbImage.refreshLocation } ?: true
            }.map {
                it.toDomainImage(
                    imageUrl = previewResolution.getImageUrl(
                        it.lowQualityUrl,
                        it.mediumQualityUrl,
                        it.sourceUrl
                    ),
                    isLiked = true
                )
            }

            data.copy(
                images = domainImages
            )
        }.resolveResult()
    }

    data class Params(
        val wallpaperLocation: WallpaperLocation? = null
    )
}