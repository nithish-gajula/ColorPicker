package com.example.colorpicker

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class LicenseActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        toolbar = findViewById(R.id.license_toolbar)
        setupStatusBar()
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        val webView = findViewById<WebView>(R.id.webViewLicense)
        val zoomInBtn = findViewById<Button>(R.id.btnZoomIn)
        val zoomOutBtn = findViewById<Button>(R.id.btnZoomOut)

        val settings: WebSettings = webView.settings
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true   // Disable default controls
        settings.displayZoomControls = false   // Hide built-in zoom buttons
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true

        webView.loadUrl("file:///android_asset/license.html")

        // Handle custom zoom buttons
        zoomInBtn.setOnClickListener { webView.zoomIn() }
        zoomOutBtn.setOnClickListener { webView.zoomOut() }
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.rich_electric_blue)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }
}