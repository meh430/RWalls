package mp.redditwalls.domain.models

import mp.redditwalls.local.enums.WallpaperLocation

data class DomainImageFolder(
    val name: String,
    val refreshEnabled: Boolean,
    val refreshLocation: String = WallpaperLocation.BOTH.name,
    val images: List<DomainImage>
)
