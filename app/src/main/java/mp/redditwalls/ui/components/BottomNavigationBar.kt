package mp.redditwalls.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import mp.redditwalls.R
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.components.IconText

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    menuItems: List<Pair<Int, IconText>>,
    selectedItem: Int,
    onSelected: (Int, Int) -> Unit
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth()
    ) {
        menuItems.forEachIndexed { index, pair ->
            NavigationBarItem(
                icon = { Icon(pair.second.icon!!, contentDescription = null) },
                label = { Text(pair.second.text) },
                selected = selectedItem == pair.first,
                onClick = { onSelected(pair.first, index) }
            )
        }
    }
}

class BottomNavigationBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private val menuItems = listOf(
        R.id.navigation_home_screen to IconText(
            context.getString(R.string.home),
            Icons.Default.Home
        ),
        R.id.navigation_favorite_images_screen to IconText(
            context.getString(R.string.favorites),
            Icons.Filled.Favorite
        ),
        R.id.navigation_discover_screen to IconText(
            context.getString(R.string.discover),
            Icons.Default.Explore
        ),
        R.id.navigation_saved_subreddits_screen to IconText(
            context.getString(R.string.saved),
            Icons.Default.CollectionsBookmark
        ),
        R.id.navigation_settings_screen to IconText(
            context.getString(R.string.settings),
            Icons.Default.Settings
        )
    )

    var selectedItem by mutableStateOf(R.id.navigation_home_screen)
    var onSelected: (Int, Int) -> Unit by mutableStateOf({ _, _ -> })

    @Composable
    override fun Content() {
        RwTheme {
            BottomNavigationBar(
                menuItems = menuItems,
                selectedItem = selectedItem,
                onSelected = { id, index ->
                    if (selectedItem != id) {
                        onSelected(id, index)
                    }
                }
            )
        }
    }
}