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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PageIndicator(
    modifier: Modifier = Modifier,
    state: PagerState
) {
    FilterChip(
        modifier = modifier,
        selected = true,
        onClick = {},
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.7f)
        ),
        label = { Text("${state.currentPage + 1} / ${state.pageCount}") }
    )
}