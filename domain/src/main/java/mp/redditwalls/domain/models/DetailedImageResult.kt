package mp.redditwalls.domain.models

data class DetailedImageResult(
    val images: List<DomainImage> = emptyList(),
    val domainSubreddit: DomainSubreddit = DomainSubreddit(),
    val folderNames: List<String> = emptyList(),
    val usePresetFolderWhenLiking: Boolean = false
)