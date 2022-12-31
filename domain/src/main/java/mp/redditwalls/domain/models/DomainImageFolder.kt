package mp.redditwalls.domain.models

import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.models.DbImageFolderWithImages
import mp.redditwalls.preferences.enums.ImageQuality

data class DomainImageFolder(
    val name: String = "",
    val refreshEnabled: Boolean = true,
    val refreshLocation: WallpaperLocation = WallpaperLocation.BOTH,
    val images: List<DomainImage> = emptyList()
)

fun DbImageFolderWithImages.toDomainImageFolder(previewResolution: ImageQuality) =
    DomainImageFolder(
        name = dbImageFolder.name,
        refreshEnabled = dbImageFolder.refreshEnabled,
        refreshLocation = WallpaperLocation.valueOf(dbImageFolder.refreshLocation),
        images = dbImages.map {
            it.toDomainImage(previewResolution = previewResolution)
        }
    )
