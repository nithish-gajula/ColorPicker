package com.example.colorpicker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowInsetsControllerCompat

class ColorDetailsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var cardColorView: CardView
    private lateinit var hexColorText: TextView
    private lateinit var hexColorTextCopy: ImageButton
    private lateinit var rgbColorText: TextView
    private lateinit var rgbTextCopy: ImageButton
    private lateinit var cmykColorText: TextView
    private lateinit var cmykTextCopy: ImageButton
    private lateinit var binaryColorText: TextView
    private lateinit var binaryTextCopy: ImageButton
    private lateinit var hslColorText: TextView
    private lateinit var hslTextCopy: ImageButton
    private lateinit var hsvColorText: TextView
    private lateinit var hsvTextCopy: ImageButton
    private lateinit var labColorText: TextView
    private lateinit var labTextCopy: ImageButton
    private lateinit var xyzColorText: TextView
    private lateinit var xyzTextCopy: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_details)

        toolbar = findViewById(R.id.about_toolbar)
        cardColorView = findViewById(R.id.show_card_color_id)
        hexColorText = findViewById(R.id.hex_color_tv_id)
        hexColorTextCopy = findViewById(R.id.hex_color_copy_ib_id)
        rgbColorText = findViewById(R.id.rgb_tv_id)
        rgbTextCopy = findViewById(R.id.rgb_copy_ib_id)
        cmykColorText = findViewById(R.id.cmyk_tv_id)
        cmykTextCopy = findViewById(R.id.cmyk_copy_ib_id)

        binaryColorText = findViewById(R.id.binary_tv_id)
        binaryTextCopy = findViewById(R.id.binary_copy_ib_id)
        hslColorText = findViewById(R.id.hsl_tv_id)
        hslTextCopy = findViewById(R.id.hsl_copy_ib_id)
        hsvColorText = findViewById(R.id.hsv_tv_id)
        hsvTextCopy = findViewById(R.id.hsv_copy_ib_id)
        labColorText = findViewById(R.id.lab_tv_id)
        labTextCopy = findViewById(R.id.lab_copy_ib_id)
        xyzColorText = findViewById(R.id.xyz_tv_id)
        xyzTextCopy = findViewById(R.id.xyz_copy_ib_id)

        setupStatusBar()
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        val colorHex = intent.getStringExtra("colorHex") ?: "#FFFFFF"
//        val colorInt = intent.getIntExtra("colorInt", Color.WHITE)

        val colorData = ColorConverter.fromHex(colorHex)

        cardColorView.setCardBackgroundColor(colorHex.toColorInt())
        hexColorText.text = colorHex
        rgbColorText.text = "RGB ${colorData.rgbInt}"
        cmykColorText.text = "CMYK ${colorData.cmyk}"
        binaryColorText.text = "BINARY ${colorData.binary}"
        hslColorText.text = "HSL ${colorData.hsl}"
        hsvColorText.text = "HSV ${colorData.hsv}"
        labColorText.text = "LAB ${colorData.lab}"
        xyzColorText.text = "XYZ ${colorData.xyz}"

        hexColorTextCopy.setOnClickListener {
            onCopyClick("Hex Color", hexColorText.text.toString())
        }

        rgbTextCopy.setOnClickListener {
            onCopyClick("RGB Color", rgbColorText.text.toString())
        }

        cmykTextCopy.setOnClickListener {
            onCopyClick("CMYK Color", cmykColorText.text.toString())
        }

        binaryTextCopy.setOnClickListener {
            onCopyClick("Binary Color", binaryColorText.text.toString())
        }

        hslTextCopy.setOnClickListener {
            onCopyClick("HSL Color", hslColorText.text.toString())
        }

        hsvTextCopy.setOnClickListener {
            onCopyClick("HSV Color", hsvColorText.text.toString())
        }

        labTextCopy.setOnClickListener {
            onCopyClick("LAB Color", labColorText.text.toString())
        }

        xyzTextCopy.setOnClickListener {
            onCopyClick("XYZ Color", xyzColorText.text.toString())
        }

    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.rich_electric_blue)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun onCopyClick(label: String, copyCode: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, copyCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, copyCode, Toast.LENGTH_SHORT).show()
    }


}