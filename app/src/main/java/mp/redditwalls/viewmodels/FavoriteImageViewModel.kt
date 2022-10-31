package mp.redditwalls.viewmodels

import kotlinx.coroutines.CoroutineScope
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.local.enums.WallpaperLocation

interface FavoriteImageViewModel {
    fun addFavoriteImage(
        coroutineScope: CoroutineScope,
        domainImage: DomainImage,
        index: Int = 0,
        refreshLocation: WallpaperLocation
    )

    fun removeFavoriteImage(
        coroutineScope: CoroutineScope,
        id: Int
    )
}