package com.example.redditwalls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.redditwalls.databinding.ActivityWallBinding
import com.example.redditwalls.misc.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog

class WallActivity : AppCompatActivity() {
    val wallArgs: WallActivityArgs by navArgs()
    private lateinit var binding: ActivityWallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.setFullScreen(window, binding.root)

        loadWallpaper()
    }

    private fun showBottomSheet() {
        BottomSheetDialog(this).apply {
            setContentView(R.layout.wall_sheet)
            show()
        }
    }

    private fun loadWallpaper() {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide.with(this)
            .load(wallArgs.image.imageLink)
            .placeholder(circularProgressDrawable)
            .centerCrop().into(binding.wallpaper)

        binding.wallpaper.setOnClickListener {
            showBottomSheet()
        }
    }
}
