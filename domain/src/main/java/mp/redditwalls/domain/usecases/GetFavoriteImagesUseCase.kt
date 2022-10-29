package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.domain.models.toDomainImage
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.preferences.PreferencesRepository
import mp.redditwalls.preferences.enums.ImageQuality

class GetFavoriteImagesUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository,
    private val preferencesRepository: PreferencesRepository
): FlowUseCase<FeedResult, WallpaperLocation>(FeedResult()) {
    override suspend operator fun invoke(params: WallpaperLocation) {
        combine(
            localImagesRepository.getDbImagesFlow(),
            preferencesRepository.getPreviewResolution()
        ) { dbImages, previewResolution ->
            val domainImages = dbImages.filter {
                it.refreshLocation == params.name
            }.map {
                it.toDomainImage(
                    imageUrl = when (previewResolution) {
                        ImageQuality.LOW -> it.lowQualityUrl
                        ImageQuality.MEDIUM -> it.mediumQualityUrl
                        ImageQuality.HIGH -> it.sourceUrl
                    },
                    isLiked = true
                )
            }

            data.copy(
                images = domainImages
            )
        }.catch { e ->
            updateData(
                DomainResult.Error(message = e.localizedMessage.orEmpty())
            )
        }.flowOn(Dispatchers.IO).collect {
            updateData(DomainResult.Success(it))
        }
    }
}