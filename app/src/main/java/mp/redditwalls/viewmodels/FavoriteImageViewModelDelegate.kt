package mp.redditwalls.viewmodels

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.usecases.AddFavoriteImageUseCase
import mp.redditwalls.domain.usecases.RemoveFavoriteImageUseCase
import mp.redditwalls.local.enums.WallpaperLocation

class FavoriteImageViewModelDelegate @Inject constructor(
    private val addFavoriteImageUseCase: AddFavoriteImageUseCase,
    private val removeFavoriteImageUseCase: RemoveFavoriteImageUseCase
) : FavoriteImageViewModel {
    override fun addFavoriteImage(
        coroutineScope: CoroutineScope,
        domainImage: DomainImage,
        index: Int,
        refreshLocation: WallpaperLocation
    ) {
        coroutineScope.launch {
            addFavoriteImageUseCase(
                AddFavoriteImageUseCase.Params(
                    image = domainImage,
                    index = index,
                    refreshLocation = refreshLocation
                )
            )
        }
    }

    override fun removeFavoriteImage(
        coroutineScope: CoroutineScope,
        id: Int
    ) {
        coroutineScope.launch {
            removeFavoriteImageUseCase(id)
        }
    }
}