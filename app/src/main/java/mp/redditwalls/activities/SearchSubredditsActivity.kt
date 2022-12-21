package mp.redditwalls.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.SearchSubredditsScreen

@AndroidEntryPoint
class SearchSubredditsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RwTheme {
                SearchSubredditsScreen()
            }
        }
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(
                Intent(context, SearchSubredditsActivity::class.java)
            )
        }
    }
}