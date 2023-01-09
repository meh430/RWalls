package mp.redditwalls.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.R
import mp.redditwalls.design.RwTheme

@Composable
fun ListOptionsDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    options: List<IconText>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {},
            title = {
                Text(text = title)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEachIndexed { index, it ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable(
                                    onClick = {
                                        onSelect(index)
                                        onDismiss()
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp),
                                imageVector = it.icon!!,
                                contentDescription = null
                            )
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun getWallpaperOptions() = listOf(
    stringResource(R.string.home_screen) to Icons.Default.Home,
    stringResource(R.string.lock_screen) to Icons.Default.Lock,
    stringResource(R.string.both_screens) to Icons.Default.Smartphone
).map { IconText(it.first, it.second) }

@Composable
fun WallpaperOptionsDialog(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.set_wallpaper),
    show: Boolean,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ListOptionsDialog(
        modifier = modifier,
        show = show,
        title = title,
        options = getWallpaperOptions(),
        onSelect = onSelect,
        onDismiss = onDismiss
    )
}

@Preview
@Composable
fun ListOptionsDialogPreview() {
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WallpaperOptionsDialog(
                show = true,
                onSelect = {},
                onDismiss = {}
            )
        }
    }
}