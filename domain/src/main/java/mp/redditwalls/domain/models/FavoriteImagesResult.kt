package mp.redditwalls.domain.models

data class FavoriteImagesResult(
    val folderNames: List<String>,
    val imageFolder: DomainImageFolder
)
