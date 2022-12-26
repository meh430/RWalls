package mp.redditwalls.domain.models

data class DetailedImageResult(
    val domainImage: DomainImage = DomainImage(),
    val domainSubreddit: DomainSubreddit = DomainSubreddit(),
    val folderName: String = "",
    val folderNames: List<String> = emptyList(),
    val usePresetFolderWhenLiking: Boolean = false
)