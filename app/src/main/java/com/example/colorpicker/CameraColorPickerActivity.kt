package com.example.colorpicker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class CameraColorPickerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var colorPreview: FrameLayout
    private lateinit var colorCodeText: TextView
    private lateinit var camera: Camera
    private lateinit var imageAnalyzer: ImageAnalysis

    private val cameraExecutor by lazy { Executors.newSingleThreadExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_color_picker)

        previewView = findViewById(R.id.previewView)
        colorPreview = findViewById(R.id.color_preview)
        colorCodeText = findViewById(R.id.color_code_text)

        setupStatusBar()
        startCamera()

        colorPreview.setOnClickListener {
            val colorHex = colorCodeText.text.toString()
            val result = Intent().apply {
                putExtra("camera_selected_color", colorHex)
            }
            setResult(RESULT_OK, result)
            finish()
        }

    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.rich_electric_blue)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ColorAnalyzer())
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(this))
    }

    private inner class ColorAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val image = imageProxy.image ?: return

            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            val width = imageProxy.width
            val height = imageProxy.height

            val centerX = width / 2
            val centerY = height / 2

            val pixelStride = image.planes[0].pixelStride
            val rowStride = image.planes[0].rowStride
            val offset = centerY * rowStride + centerX * pixelStride

            val y = data[offset].toInt() and 0xFF
            val colorInt = Color.rgb(y, y, y)

            runOnUiThread {
                colorPreview.setBackgroundColor(colorInt)
//                colorCodeText.text = String.format("#%06X", 0xFFFFFF and colorInt)
                colorCodeText.text = String.format("#%06X", colorInt and 0x00FFFFFF)

            }

            imageProxy.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
