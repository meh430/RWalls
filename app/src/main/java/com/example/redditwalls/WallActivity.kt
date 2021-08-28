package com.example.redditwalls

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.navigation.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.redditwalls.databinding.ActivityWallBinding
import com.example.redditwalls.databinding.WallSheetBinding
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.models.PostInfo
import com.example.redditwalls.models.Resource
import com.example.redditwalls.viewmodels.WallViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import android.view.MotionEvent
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import androidx.appcompat.content.res.AppCompatResources
import kotlin.math.abs


@AndroidEntryPoint
class WallActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private val wallArgs: WallActivityArgs by navArgs()
    private lateinit var binding: ActivityWallBinding
    private val wallViewModel: WallViewModel by viewModels()

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

        wallViewModel.initialize(wallArgs.image)

        Utils.setFullScreen(window, binding.root)
        loadWallpaper()
        loadPostInfo(sheetBinding)

        detector = GestureDetector(this, this)
        binding.info.setOnClickListener {
            wallSheet.show()
        }

        setUpFavorite()
        binding.favorite.setOnClickListener {
            wallViewModel.toggleFavorite()
        }
    }

    private fun setUpFavorite() {
        binding.favorite.setOnClickListener {
            wallViewModel.toggleFavorite()
        }

        wallViewModel.isFavorite.observe(this) {
            val icon = if (it) filledHeartIcon else heartIcon
            binding.favorite.icon = icon
        }
    }

    private fun loadWallpaper() {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(Color.RED)
        circularProgressDrawable.start()
        Glide.with(this)
            .load(wallArgs.image.imageLink)
            .placeholder(circularProgressDrawable)
            .centerCrop().into(binding.wallpaper)
    }

    private fun loadPostInfo(sheetBinding: WallSheetBinding) {
        wallViewModel.getPostInfo(
            postLink = wallArgs.image.postLink,
            imageLink = wallArgs.image.imageLink,
            context = this,
            resolution = Utils.getResolution(context = this)
        )
        wallViewModel.postInfo.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS, Resource.Status.ERROR -> sheetBinding.postInfo = it.data
                Resource.Status.LOADING -> sheetBinding.postInfo = PostInfo.loading()
            }
        }
    }


    // Gestures

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onDown(event: MotionEvent) = false

    override fun onShowPress(event: MotionEvent) {
        // NO-OP
    }

    override fun onSingleTapUp(event: MotionEvent) = false

    override fun onScroll(evt1: MotionEvent, evt2: MotionEvent, p1: Float, p2: Float) = false

    override fun onLongPress(event: MotionEvent) {
        // NO-OP
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, vX: Float, vY: Float): Boolean {
        val SWIPE_DISTANCE_THRESHOLD = 50
        val SWIPE_VELOCITY_THRESHOLD = 50
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
}
