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

@AndroidEntryPoint
class WallActivity : AppCompatActivity() {
    private val wallArgs: WallActivityArgs by navArgs()
    private lateinit var binding: ActivityWallBinding
    private val wallViewModel: WallViewModel by viewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.setFullScreen(window, binding.root)
        loadWallpaper()
        loadPostInfo(sheetBinding)
    }

    private fun showBottomSheet() {
        wallSheet.show()
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

        binding.wallpaper.setOnClickListener {
            showBottomSheet()
        }
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
}
