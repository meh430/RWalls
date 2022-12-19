package mp.redditwalls.local.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import mp.redditwalls.local.enums.WallpaperLocation

@Entity(tableName = "ImageFolders")
data class DbImageFolder(
    @PrimaryKey
    val name: String,
    val refreshEnabled: Boolean,
    val refreshLocation: String = WallpaperLocation.BOTH.name
) {
    companion object {
        const val DEFAULT_FOLDER_NAME = "default"
    }
}

data class DbImageFolderWithImages(
    @Embedded val dbImageFolder: DbImageFolder,
    @Relation(
        parentColumn = "name",
        entityColumn = "imageFolderName"
    )
    val dbImages: List<DbImage>
)