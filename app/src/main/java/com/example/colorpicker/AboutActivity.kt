package com.example.colorpicker

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class AboutActivity : AppCompatActivity() {
    private lateinit var gmail: ImageView
    private lateinit var github: ImageView
    private lateinit var instagram: ImageView
    private lateinit var linkedin: ImageView
    private lateinit var gradientBackgroundLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gradient_background_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gmail = findViewById(R.id.gmail_img_id)
        github = findViewById(R.id.github_img_id)
        instagram = findViewById(R.id.instagram_img_id)
        linkedin = findViewById(R.id.linkedin_img_id)
        gradientBackgroundLayout = findViewById(R.id.gradient_background_layout)

        gradientAnimationLayout(gradientBackgroundLayout)

        gmail.setOnClickListener(View.OnClickListener {
            val uri = getString(R.string.email_info).toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })

        github.setOnClickListener(View.OnClickListener {
            val uri = getString(R.string.github_info).toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })

        instagram.setOnClickListener(View.OnClickListener {
            val uri = getString(R.string.instagram_info).toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })

        linkedin.setOnClickListener(View.OnClickListener {
            val uri = getString(R.string.linkedin_info).toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        })
    }

    private fun gradientAnimationLayout(targetLayout: LinearLayout) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,  // 45° angle (top-left to bottom-right)
            intArrayOf(
                0xFFFFE6F0.toInt(),
                0xFFE6FFF5.toInt(),
                0xFFE6F7FF.toInt()
            )
        ).apply {
            cornerRadius = 0f // No rounded corners for full background
        }

        targetLayout.background = gradientDrawable

        val colors = arrayOf(
            0xFFFFE6F0.toInt(),
            0xFFE6FFF5.toInt(),
            0xFFE6F7FF.toInt()
        )

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE

            addUpdateListener { anim ->
                val fraction = anim.animatedFraction

                val startColor = ArgbEvaluator().evaluate(fraction, colors[0], colors[1]) as Int
                val middleColor = ArgbEvaluator().evaluate(fraction, colors[1], colors[2]) as Int
                val endColor = ArgbEvaluator().evaluate(fraction, colors[2], colors[0]) as Int

                gradientDrawable.colors = intArrayOf(startColor, middleColor, endColor)
            }
        }
        animator.start()
    }

}