package com.example.colorpicker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnImageEventListener


class ImageColorPickerActivity : Activity() {

    private lateinit var imageView: SubsamplingScaleImageView
    private lateinit var saveColorButton: Button
    private lateinit var aimView: View
    private var imageUri: Uri? = null

    private lateinit var loadedBitmap: Bitmap
    private var sampledColor = 0
    private var updateHandler: Handler = Handler()
    private var updateFrequencyMs = 500
    private val maxPixels = 64
    private val maxZoom = 10.0f
    private var lastSampleTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_color_picker)

        imageView = findViewById(R.id.image_view)
        aimView = findViewById(R.id.resizable_aim)
        saveColorButton = findViewById(R.id.button_save_color)

//        // Get passed image URI
        imageUri = intent?.data
        if (imageUri == null) {
            Toast.makeText(this, "No image provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        imageUri?.let { loadImage(it) }

        setupImageView()
        setupSaveButton()

        updateHandler.postDelayed(this::updateColorSampling, updateFrequencyMs.toLong())

    }

    private fun setupSaveButton() {
        saveColorButton.setOnClickListener { v ->
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            val hexColor = String.format("#%06X", (0xFFFFFF and sampledColor))

            val result = Intent().apply {
                putExtra("image_selected_color", hexColor)
            }
            setResult(RESULT_OK, result)
            finish()
        }
    }

    private fun loadImage(uri: Uri) {
        Thread {
            try {
                loadedBitmap = Glide.with(applicationContext)
                    .asBitmap()
                    .load(uri)
                    .submit()
                    .get()

                // Optional downscaling
                if (loadedBitmap.height > 4096) {
                    val targetHeight = 4096
                    val targetWidth = loadedBitmap.width * targetHeight / loadedBitmap.height
                    loadedBitmap = loadedBitmap.scale(targetWidth, targetHeight, false)
                }

                runOnUiThread {
                    imageView.setImage(ImageSource.cachedBitmap(loadedBitmap))
                    aimView.visibility = View.VISIBLE
                    saveColorButton.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
//                Log.e("ImageColorPickerActivity", "Failed to load image: " + e.message)
                showErrorDialog("Failed to load image: " + e.message);
            }
        }.start()
    }

    private fun setupImageView() {
        imageView.maxScale = maxZoom
        imageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)
        imageView.setOnImageEventListener(object : OnImageEventListener {
            override fun onImageLoaded() {
                updateHandler.postDelayed({ updateColorSampling() }, updateFrequencyMs.toLong())
            }

            override fun onImageLoadError(e: java.lang.Exception) {
                showErrorDialog("Could not display image.")
            }

            // Unused
            override fun onPreviewLoadError(e: java.lang.Exception) {}
            override fun onReady() {}
            override fun onPreviewReleased() {}
            override fun onTileLoadError(e: java.lang.Exception) {}
        })
    }

    private fun updateColorSampling() {
        if (System.currentTimeMillis() - lastSampleTime < updateFrequencyMs) return
        lastSampleTime = System.currentTimeMillis()

        if (imageView.isReady) {
            val center = imageView.viewToSourceCoord(
                aimView.x + aimView.width / 2f,
                aimView.y + aimView.height / 2f
            )

            if (center != null && loadedBitmap != null) {
                val x = center.x.toInt().coerceIn(0, loadedBitmap.width - 1)
                val y = center.y.toInt().coerceIn(0, loadedBitmap.height - 1)

                sampledColor = loadedBitmap[x, y]

                // Update UI with sampled color
                aimView.background.setTint(sampledColor)
                saveColorButton.setBackgroundColor(sampledColor)
                saveColorButton.text = String.format("#%06X", (0xFFFFFF and sampledColor))
            }
        }

        updateHandler.postDelayed(this::updateColorSampling, updateFrequencyMs.toLong())
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onPause() {
        super.onPause()
        updateHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        updateHandler.postDelayed(this::updateColorSampling, updateFrequencyMs.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        updateHandler.removeCallbacksAndMessages(null)
    }

}
