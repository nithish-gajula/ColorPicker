package com.example.colorpicker

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LicenseActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_license)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.licence)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.license_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

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
}