package mp.redditwalls.domain.models

data class FavoriteImagesResult(
    val folderNames: List<String> = emptyList(),
    val imageFolder: DomainImageFolder = DomainImageFolder()
)
