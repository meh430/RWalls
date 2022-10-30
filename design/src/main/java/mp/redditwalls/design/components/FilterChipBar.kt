package mp.redditwalls.design.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import mp.redditwalls.design.RwTheme

// -1 if no selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipBar(
    modifier: Modifier = Modifier,
    filters: List<Pair<String, ImageVector?>>,
    initialSelection: Int = 0,
    onSelectionChanged: (Int) -> Unit
) {
    var selectedChipIndex by remember { mutableStateOf(initialSelection) }
    FlowRow(
        modifier = modifier.fillMaxWidth()
    ) {
        filters.forEachIndexed { index, it ->
            val selected = selectedChipIndex == index
            FilterChip(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = selected,
                onClick = {
                    selectedChipIndex = if (selected) {
                        -1
                    } else {
                        index
                    }
                    onSelectionChanged(selectedChipIndex)
                },
                label = { Text(text = it.first) },
                leadingIcon = {
                    it.second?.let { imageVector ->
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = imageVector,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun FilterChipBarPreview() {
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FilterChipBar(
                modifier = Modifier.padding(8.dp),
                filters = listOf(
                    "option 1" to Icons.Default.Favorite,
                    "option 2" to Icons.Default.Lock,
                    "option 3" to null,
                    "option 4" to null,
                    "option 5" to null,
                    "option 6" to null,
                    "option 7" to null,
                )
            ) {}
        }
    }
}