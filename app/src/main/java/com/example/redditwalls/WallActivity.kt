package com.example.redditwalls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.example.redditwalls.databinding.ActivityWallBinding
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class WallActivity : AppCompatActivity() {
    val wallArgs: WallActivityArgs by navArgs()
    private lateinit var binding: ActivityWallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        Glide.with(this).load(wallArgs.image.imageLink).centerCrop().into(binding.wallpaper)
    }
}