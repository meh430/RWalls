package mp.redditwalls.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.viewmodels.SearchImagesScreenViewModel

@Composable
fun SearchImagesScreen(vm: SearchImagesScreenViewModel = viewModel()) {
    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult
}