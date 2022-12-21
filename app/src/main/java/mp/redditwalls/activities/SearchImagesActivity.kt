package mp.redditwalls.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.SearchImagesScreen
import mp.redditwalls.utils.parcelable

@AndroidEntryPoint
class SearchImagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arguments = intent.parcelable<SearchImagesActivityArguments>(
            SEARCH_IMAGES_ACTIVITY_ARGUMENTS
        ) ?: return

        setContent {
            RwTheme {
                SearchImagesScreen(
                    subreddit = arguments.subreddit,
                    query = arguments.query
                )
            }
        }
    }

    companion object {
        private const val SEARCH_IMAGES_ACTIVITY_ARGUMENTS = "SEARCH_IMAGES_ACTIVITY_ARGUMENTS"

        fun launch(
            context: Context,
            arguments: SearchImagesActivityArguments
        ) {
            context.startActivity(
                Intent(context, SearchImagesActivity::class.java).apply {
                    putExtra(SEARCH_IMAGES_ACTIVITY_ARGUMENTS, arguments)
                }
            )
        }
    }
}

@Parcelize
data class SearchImagesActivityArguments(
    val subreddit: String? = null,
    val query: String? = null
) : Parcelable