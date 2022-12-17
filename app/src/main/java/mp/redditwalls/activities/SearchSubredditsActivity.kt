package mp.redditwalls.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.SearchSubredditsScreen

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
        fun getIntent(context: Context) = Intent(context, SearchSubredditsActivity::class.java)
    }
}