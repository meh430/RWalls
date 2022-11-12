package mp.redditwalls

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigator
import androidx.navigation.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt
import mp.redditwalls.databinding.ActivityWallBinding
import mp.redditwalls.databinding.WallSheetBinding
import mp.redditwalls.datasources.RWApi
import mp.redditwalls.utils.Utils
import mp.redditwalls.utils.launchBrowser
import mp.redditwalls.utils.toPx
import mp.redditwalls.models.Image
import mp.redditwalls.models.PostInfo
import mp.redditwalls.models.Resource
import mp.redditwalls.viewmodels.SettingsViewModel
import mp.redditwalls.viewmodels.WallViewModel


@AndroidEntryPoint
class WallActivity : AppCompatActivity(), GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    companion object {
        const val SWIPE_DISTANCE_THRESHOLD = 50
        const val SWIPE_VELOCITY_THRESHOLD = 50
    }

    private val wallArgs: WallActivityArgs by navArgs()
    private lateinit var binding: ActivityWallBinding
    private val wallViewModel: WallViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    private lateinit var detector: GestureDetector

    private val sheetBinding: WallSheetBinding by lazy {
        WallSheetBinding.inflate(
            LayoutInflater.from(this)
        )
    }
    private val wallSheet: BottomSheetDialog by lazy {
        val wallSheet = BottomSheetDialog(this)
        wallSheet.setContentView(sheetBinding.root)
        wallSheet
    }

    private val filledHeartIcon: Drawable? by lazy {
        AppCompatResources.getDrawable(this, R.drawable.ic_favorite_filled)
    }

    private val heartIcon: Drawable? by lazy {
        AppCompatResources.getDrawable(this, R.drawable.ic_favorite)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.apply {
            sharedElementEnterTransition.duration = 200
            sharedElementExitTransition.duration = 200
        }

        try {
            wallViewModel.initialize(wallArgs.image, wallArgs.postId, wallArgs.subreddit)
        } catch (e: Exception) {
            intent.data?.toString()?.let {
                val (subreddit, id) = RWApi.extractPostLinkInfo(it)
                wallViewModel.initialize(null, id, subreddit)
            }
        }
        Utils.setFullScreen(window, binding.root)
        setBackButtonMargins()

        observeImage()

        detector = GestureDetector(this, this)

        setUpFavorite()
        addListeners()
    }

    private fun setBackButtonMargins() {
        20.toPx.roundToInt().let {
            val statusHeight = Utils.getStatusBarHeight(this)

            val params = (binding.backButton.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(it, statusHeight + 10, it, it)
        }
    }

    private fun observeImage() {
        wallViewModel.currentImage.observe(this) {
            binding.bottomButtons.isVisible = false
            binding.error.error.isVisible = false
            binding.loading.isVisible = false
            binding.wallpaper.isVisible = false

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.bottomButtons.isVisible = true
                    binding.wallpaper.isVisible = true
                    it.data?.let { image ->
                        wallViewModel.setUpFavorite(image)
                        loadWallpaper(image)
                        loadPostInfo(sheetBinding, image)
                    }
                }
                Resource.Status.LOADING -> {
                    binding.loading.isVisible = true
                }
                Resource.Status.ERROR -> {
                    binding.error.error.isVisible = true
                    binding.error.errorLabel.text = it.errorMessage
                }
            }
        }
    }

    private fun addListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.info.setOnClickListener {
            wallSheet.show()
        }

        binding.favorite.setOnClickListener {
            wallViewModel.toggleFavorite()
        }

        sheetBinding.setWallpaper.setOnClickListener {
            setWallpaper()
        }

        sheetBinding.saveWallpaper.setOnClickListener {
            getBitmap()?.let {
                Utils.saveBitmap(
                    it,
                    sheetBinding.postTitle.text?.toString(),
                    this,
                    settingsViewModel.toastEnabled()
                )
            }
        }

        sheetBinding.toPost.setOnClickListener {
            val image = wallViewModel.currentImage.value?.data
            if (image != null) {
                image.postLink.launchBrowser(this)
            } else {
                Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
            }
        }

        sheetBinding.author.setOnClickListener {
            wallViewModel.postInfo.value?.data?.author?.let { author ->
                "${RWApi.BASE}/$author".launchBrowser(this)
            }
        }

        sheetBinding.subreddit.setOnClickListener {
            wallViewModel.postInfo.value?.data?.subreddit?.let { sub ->
                "${RWApi.BASE}/$sub".launchBrowser(this)
            }
        }
    }

    private fun setWallpaper() {
        if (isFinishing) {
            return
        } else if (wallViewModel.postInfo.value?.data == null) {
            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
            return
        }

        wallpaperHelper.showLocationPickerDialog(this) { location ->
            getBitmap()?.let {
                if (wallpaperHelper.setBitmapAsWallpaper(this, it, location)) {
                    wallViewModel.insertHistory(location)
                }
            }
        }
    }

    private fun getBitmap() =
        if (binding.wallpaper.drawable == null || binding.wallpaper.drawable is CircularProgressDrawable) {
            Toast.makeText(this, "Loading image...", Toast.LENGTH_SHORT).show()
            null
        } else {
            (binding.wallpaper.drawable as BitmapDrawable).bitmap
        }

    private fun setUpFavorite() {
        wallViewModel.isFavorite.observe(this) {
            val icon = if (it) filledHeartIcon else heartIcon
            binding.favorite.setImageDrawable(icon)
        }
    }

    private fun loadWallpaper(image: Image) {
        Glide.with(this)
            .load(image.imageLink)
            .placeholder(Utils.getImageLoadingDrawable(this))
            .centerCrop().into(binding.wallpaper)
    }

    private fun loadPostInfo(sheetBinding: WallSheetBinding, image: Image) {
        wallViewModel.getPostInfo(
            postLink = image.postLink,
            imageLink = image.imageLink,
            context = this,
            resolution = Utils.getResolution(context = this)
        )
        wallViewModel.postInfo.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> sheetBinding.postInfo = it.data
                Resource.Status.LOADING -> sheetBinding.postInfo = PostInfo.loading()
                Resource.Status.ERROR -> sheetBinding.postInfo = PostInfo.error(it.errorMessage)
            }
        }
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }


    // Gestures

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, vX: Float, vY: Float): Boolean {
        if (e1 == null || e2 == null) {
            return false
        }

        val distanceX: Float = e2.x - e1.x
        val distanceY: Float = e2.y - e1.y
        if (abs(distanceX) > abs(distanceY) && abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && abs(vX) > SWIPE_VELOCITY_THRESHOLD) {
            return true
        } else if (abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && abs(vY) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceY > 0) {
                wallSheet.hide()
            } else {
                wallSheet.show()
            }
            return true
        }
        return false
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        wallViewModel.toggleFavorite()
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean = true

    override fun onDoubleTapEvent(event: MotionEvent): Boolean = true

    override fun onDown(event: MotionEvent) = false

    override fun onShowPress(event: MotionEvent) {
        // NO-OP
    }

    override fun onSingleTapUp(event: MotionEvent) = false

    override fun onScroll(evt1: MotionEvent, evt2: MotionEvent, p1: Float, p2: Float): Boolean {
        return false
    }

    override fun onLongPress(event: MotionEvent) {
        setWallpaper()
    }
}
