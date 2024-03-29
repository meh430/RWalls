package mp.redditwalls.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.ImageAlbum
import mp.redditwalls.design.components.ImageFolderRadioDialog
import mp.redditwalls.design.components.LikeAnimation
import mp.redditwalls.design.components.PageIndicator
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.SetWallpaperDialog
import mp.redditwalls.ui.components.WallpaperInfoCard
import mp.redditwalls.utils.DownloadUtils
import mp.redditwalls.utils.Utils
import mp.redditwalls.utils.launchBrowser
import mp.redditwalls.utils.writePermissionGranted
import mp.redditwalls.viewmodels.WallpaperScreenViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WallpaperScreen(
    vm: WallpaperScreenViewModel = viewModel(),
    wallpaperHelper: WallpaperHelper,
    downloadUtils: DownloadUtils,
    arguments: WallpaperActivityArguments,
    onWritePermissionRequest: () -> Unit
) {
    val context = LocalContext.current
    val statusBarHeight =
        WindowInsets.systemBarsIgnoringVisibility.asPaddingValues().calculateTopPadding()

    val pagerState = rememberPagerState()
    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult

    val imageUrls = remember(uiState.images) {
        uiState.images.map { it.imageUrl.url }
    }

    var expanded by remember { mutableStateOf(false) }
    var playAnimation by remember { mutableStateOf(false) }

    val onLikeClick = { currentImage: ImageItemUiState, isLiked: Boolean ->
        if (uiState.usePresetFolderWhenLiking || !isLiked) {
            vm.favoriteImageViewModel.onLikeClick(
                image = currentImage,
                isLiked = isLiked,
                folderName = null
            )
        } else {
            vm.showFolderSelectDialog()
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .consumedWindowInsets(innerPadding)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (_, y) = dragAmount
                        when {
                            y > 0 -> {
                                // swipe down
                                expanded = false
                                vm.showUi()
                            }
                            y < 0 -> {
                                // swipe up
                                expanded = true
                                vm.showUi()
                            }
                        }
                    }
                }
        ) {
            when (uiResult) {
                is UiResult.Error -> ErrorState(errorMessage = uiResult.errorMessage.orEmpty()) {
                    vm.fetchWallpaper(arguments.imageId.networkId)
                }
                is UiResult.Loading -> ThreeDotsLoader()
                is UiResult.Success -> {
                    val view = LocalView.current
                    val currentImage = remember(uiState.images, pagerState.currentPage) {
                        uiState.images[pagerState.currentPage]
                    }

                    BackHandler {
                        if (expanded) {
                            expanded = false
                        } else {
                            (context as? Activity)?.finish()
                        }
                    }
                    SetWallpaperDialog(
                        wallpaperHelper = wallpaperHelper,
                        context = context,
                        image = currentImage.takeIf {
                            vm.showSetWallpaperDialog
                        },
                        onDismiss = {
                            vm.dismissSetWallpaperDialog()
                            Utils.setFullScreen((context as Activity).window, view)
                        }
                    )
                    ImageFolderRadioDialog(
                        show = vm.showFolderSelectDialog,
                        options = uiState.folderNames,
                        initialSelection = currentImage.folderName,
                        onSubmit = { name ->
                            vm.favoriteImageViewModel.onLikeClick(
                                image = currentImage,
                                isLiked = true,
                                folderName = name
                            )
                        },
                        onDismiss = {
                            vm.dismissFolderSelectDialog()
                            Utils.setFullScreen((context as Activity).window, view)
                        }
                    )
                    ImageAlbum(
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState,
                        imageUrls = imageUrls,
                        onClick = {
                            vm.toggleUiVisibility()
                            Utils.setFullScreen((context as Activity).window, view)
                        },
                        onLongClick = vm::showSetWallpaperDialog,
                        onDoubleClick = {
                            if (!currentImage.isLiked.value) {
                                onLikeClick(currentImage, true)
                            }
                            playAnimation = true
                        }
                    )
                    LikeAnimation(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        show = playAnimation,
                        onAnimationComplete = { playAnimation = false }
                    )
                    AnimatedVisibility(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        enter = fadeIn(),
                        exit = fadeOut(),
                        visible = !uiState.shouldHideUi
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = statusBarHeight,
                                    horizontal = 12.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BackButton(
                                modifier = Modifier.background(
                                    MaterialTheme.colorScheme.surface.copy(0.7f),
                                    CircleShape
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                                onClick = { (context as? Activity)?.finish() }
                            )
                            if (pagerState.pageCount > 1) {
                                PageIndicator(state = pagerState)
                            }
                        }
                    }
                    AnimatedVisibility(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        enter = fadeIn(),
                        exit = fadeOut(),
                        visible = !uiState.shouldHideUi
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            WallpaperInfoCard(
                                image = currentImage,
                                subreddit = uiState.subreddit,
                                expanded = expanded,
                                onExpand = { expanded = !expanded },
                                navigateToPost = {
                                    currentImage.postUrl.launchBrowser(
                                        context
                                    )
                                },
                                navigateToUser = {
                                    currentImage.authorUrl.launchBrowser(
                                        context
                                    )
                                },
                                onLikeClick = { isLiked -> onLikeClick(currentImage, isLiked) },
                                onFolderNameClick = vm::showFolderSelectDialog,
                                onSaveClick = {
                                    vm.savedSubredditViewModel.onSaveClick(uiState.subreddit, it)
                                },
                                onSetWallpaperClick = vm::showSetWallpaperDialog,
                                onDownloadClick = {
                                    if (context.writePermissionGranted()) {
                                        downloadUtils.downloadImage(currentImage.imageUrl.url)
                                    } else {
                                        onWritePermissionRequest()
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.fetchWallpaper(arguments.imageId.networkId)
        pagerState.scrollToPage(arguments.imageId.index)
    }
}
