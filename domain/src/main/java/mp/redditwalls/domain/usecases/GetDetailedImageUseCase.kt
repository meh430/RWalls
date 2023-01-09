package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.DetailedImageResult
import mp.redditwalls.domain.models.toDomainImages
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.repositories.LocalImageFoldersRepository
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.local.toNetworkIdToDbImageMap
import mp.redditwalls.network.models.GalleryItem
import mp.redditwalls.network.repositories.ImgurRepository
import mp.redditwalls.network.repositories.NetworkImagesRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetDetailedImageUseCase @Inject constructor(
    private val networkImagesRepository: NetworkImagesRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val localImagesRepository: LocalImagesRepository,
    private val localImageFoldersRepository: LocalImageFoldersRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val imgurRepository: ImgurRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<DetailedImageResult, String>(DetailedImageResult()) {
    // takes network image id
    override fun execute() = combine(
        paramsFlow,
        preferencesRepository.getAllPreferences(),
        localSubredditsRepository.getDbSubreddits(),
        localImagesRepository.getDbImagesFlow(),
        localImageFoldersRepository.getDbImageFolderNames()
    ) { params, preferences, dbSubreddits, dbImages, folderNames ->
        val dbSubredditNames = dbSubreddits.map { it.name }.toSet()
        val dbImageIds = dbImages.toNetworkIdToDbImageMap()

        val networkImage = networkImagesRepository.getImage(params).let {
            if (it.imgurAlbumId.isNotEmpty()) {
                it.copy(
                    galleryItems = imgurRepository.getAlbum(
                        it.imgurAlbumId
                    ).images.map { imgurImage ->
                        GalleryItem(
                            lowQualityUrl = imgurImage.link,
                            mediumQualityUrl = imgurImage.link,
                            sourceUrl = imgurImage.link
                        )
                    }
                )
            } else {
                it
            }
        }

        val domainSubreddit = networkSubredditsRepository.getSubredditDetail(
            networkImage.subredditName
        ).toDomainSubreddit(
            isSaved = networkImage.subredditName in dbSubredditNames
        )

        val images = networkImage.toDomainImages(preferences.previewResolution) { imageId ->
            dbImageIds[imageId.networkId]?.find { it.id == imageId.dbImageId }
        }

        DetailedImageResult(
            images = images,
            domainSubreddit = domainSubreddit,
            folderNames = folderNames,
            usePresetFolderWhenLiking = preferences.usePresetFolderWhenLiking
        )
    }
}