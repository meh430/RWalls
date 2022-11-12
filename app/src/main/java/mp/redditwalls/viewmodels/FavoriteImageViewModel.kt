package mp.redditwalls.viewmodels

import kotlinx.coroutines.CoroutineScope
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.models.ImageItemUiState

interface FavoriteImageViewModel {
    var coroutineScope: CoroutineScope

    fun addFavoriteImage(
        domainImage: DomainImage,
        index: Int = 0,
        refreshLocation: WallpaperLocation
    )

    fun removeFavoriteImage(
        id: String
    )

    fun onLikeClick(image: ImageItemUiState, isLiked: Boolean)
}