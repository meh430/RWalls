package mp.redditwalls.design.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageAlbum(
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(),
    imageUrls: List<String>
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        count = imageUrls.size
    ) { index ->
        imageUrls.getOrNull(index)?.let { url ->
            Image(
                modifier = Modifier.fillMaxWidth(),
                imageUrl = url,
                showRipple = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int = 0,
    pageCount: Int = 0
) {
    FilterChip(
        modifier = modifier,
        selected = true,
        onClick = {},
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.6f)
        ),
        label = { Text("${currentPage + 1} / $pageCount") }
    )
}