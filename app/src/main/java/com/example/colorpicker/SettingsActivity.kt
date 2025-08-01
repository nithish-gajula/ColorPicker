package com.example.colorpicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Setup Toolbar with Back Navigation
        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)

        setupStatusBar()
        toolbar.setNavigationOnClickListener { finish() }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }

    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.rich_electric_blue)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }
}