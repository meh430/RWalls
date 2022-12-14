package mp.redditwalls.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.RwTheme

@Composable
fun ClickableTextItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun ClickableTextPreview() {
    RwTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ClickableTextItem(
                modifier = Modifier.padding(12.dp),
                title = "Enable this setting we have here",
                subtitle = "Explain in detail what this setting does, for example what if it is a really really long setting explanation, then it should wrap around onto the other line into as many lines as we would even need.",
                onClick = {}
            )
        }
    }
}