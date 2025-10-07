package com.nithishgajula.colorpicker

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog
import me.jfenn.colorpickerdialog.views.picker.HSVPickerView
import me.jfenn.colorpickerdialog.views.picker.PresetPickerView
import me.jfenn.colorpickerdialog.views.picker.RGBPickerView
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import net.mm2d.color.chooser.ColorChooserDialog.TAB_RGB

class MainActivity : AppCompatActivity(), ColorItemListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var colorPickerButton: MaterialButton
    private lateinit var settings: ImageButton
    private lateinit var selectPhoto: ImageButton
    private lateinit var adapter: ColorAdapter
    private lateinit var lottieAnimation: LottieAnimationView
    private var color: Int = "#8C9EFF".toColorInt()
    private val colorList = mutableListOf<ColorItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerColors)
        settings = findViewById(R.id.settings_id)
        selectPhoto = findViewById(R.id.select_photo_id)
        colorPickerButton = findViewById(R.id.color_picker_id)
        lottieAnimation = findViewById(R.id.lottieView)

        setupRecyclerView()
        colorList.addAll(StorageUtil.loadColors(this))
        adapter.notifyDataSetChanged()

        gradientAnimationButton()
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true)

        if (colorList.isEmpty()) {
            lottieAnimation.visibility = View.VISIBLE
            lottieAnimation.playAnimation()
        } else {
            lottieAnimation.visibility = View.GONE
            lottieAnimation.cancelAnimation()
        }

        settings.setOnClickListener { v ->
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        selectPhoto.setOnClickListener {
            showBottomDialog()
        }

        colorPickerButton.setOnClickListener {

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val library = prefs.getString("pref_library", "ColorChooser") ?: "ColorChooser"
            val withAlpha = prefs.getBoolean("pref_with_alpha", false)
            val style = prefs.getString("pref_initial_style", "PALETTE") ?: "PALETTE"

            when (library) {
                "ColorChooser" -> {
                    val tab = when (style) {
                        "PALETTE" -> TAB_PALETTE
                        "HSV" -> TAB_HSV
                        "RGB" -> TAB_RGB
                        else -> TAB_PALETTE
                    }
                    ColorChooserDialog.show(
                        this,
                        "colorRequest",
                        color,
                        withAlpha,
                        tab
                    )
                    ColorChooserDialog.registerListener(this, "colorRequest", { selectedColor ->
                        handleColorSelected(selectedColor, withAlpha)
                    })

                }

                "ColorPickerDialog" -> {
                    val dialog = ColorPickerDialog()
                        .withTitle("Pick a Color")
                        .withAlphaEnabled(withAlpha)
                        .apply {
                            when (style) {
                                "PALETTE" -> withPickers(
                                    PresetPickerView::class.java,
                                    HSVPickerView::class.java,
                                    RGBPickerView::class.java,
                                )

                                "HSV" -> withPickers(
                                    HSVPickerView::class.java,
                                    PresetPickerView::class.java,
                                    RGBPickerView::class.java,
                                )

                                "RGB" -> withPickers(
                                    RGBPickerView::class.java,
                                    PresetPickerView::class.java,
                                    HSVPickerView::class.java,
                                )

                                else -> withPickers(
                                    PresetPickerView::class.java,
                                    HSVPickerView::class.java,
                                    RGBPickerView::class.java
                                )
                            }
                        }
                        .withColor(color)
                        .withListener { _, color ->
                            handleColorSelected(color, withAlpha)
                        }
                    dialog.show(supportFragmentManager, "colorPicker")
                    supportFragmentManager.executePendingTransactions()

                    val frag = supportFragmentManager.findFragmentByTag("colorPicker")
                    frag?.view?.post {
                        val id = resources.getIdentifier("tabLayout", "id", packageName)
                        val tabLayout = frag.view?.findViewById<com.google.android.material.tabs.TabLayout>(id)
                        tabLayout?.setTabTextColors(Color.GRAY, Color.BLACK)
                    }
                }
            }
        }
    }

    private fun handleColorSelected(selectedColor: Int, withAlpha: Boolean) {
        val colorCode = if (withAlpha) {
            String.format("#%08X", selectedColor)  // ARGB
        } else {
            String.format("#%06X", selectedColor and 0x00FFFFFF)  // RGB only
        }

        // Smooth fade-out animation for Lottie
        if (lottieAnimation.isVisible) {
            lottieAnimation.animate().alpha(0f).setDuration(150).withEndAction {
                lottieAnimation.visibility = View.GONE
                lottieAnimation.cancelAnimation()
                lottieAnimation.alpha = 1f // reset for future use
            }.start()
        }

        // Add color to adapter
        adapter.addColor(selectedColor, colorCode)

        // Save asynchronously to avoid blocking UI
        lifecycleScope.launch(Dispatchers.IO) {
            StorageUtil.saveColors(this@MainActivity, adapter.getColors())
        }

        // Smooth scroll to top without layout conflict
        recyclerView.post {
            recyclerView.scrollToPosition(0)
        }
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        val cameraLayout = dialog.findViewById<LinearLayout>(R.id.layoutCamera)
        val imageLayout = dialog.findViewById<LinearLayout>(R.id.layoutImage)
        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)

        cameraLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Feature in development", Toast.LENGTH_SHORT).show()
        }

        imageLayout.setOnClickListener {
            dialog.dismiss()
//            val intent = Intent(Intent.ACTION_PICK).apply {
//                type = "image/*"
//            }
//            pickImageLauncher.launch(intent)
            Toast.makeText(this, "Feature in development", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }
    }

    private fun setupRecyclerView() {
        adapter = ColorAdapter(colorList, this) { clickedColor ->
            // Launch ColorDetailsActivity with the selected color
            val intent = Intent(this, ColorDetailsActivity::class.java).apply {
                putExtra("colorHex", clickedColor.hexCode)
                putExtra("colorInt", clickedColor.colorInt)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
    }

    override fun onDeleteClick(position: Int) {
        Toast.makeText(this, "${colorList[position].hexCode} Deleted!", Toast.LENGTH_SHORT).show()
        adapter.removeColor(position)
        StorageUtil.saveColors(this, adapter.getColors())
        if (colorList.isEmpty()) {
            lottieAnimation.visibility = View.VISIBLE
            lottieAnimation.playAnimation()
        }
    }

    override fun onCopyClick(hexCode: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Color Hex", hexCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$hexCode Copied!", Toast.LENGTH_SHORT).show()
    }

    private fun gradientAnimationButton() {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(0xFFFF6F61.toInt(), 0xFFFFD700.toInt(), 0xFF20B2AA.toInt())
        ).apply {
            cornerRadius = 25f
        }
        // Set it explicitly, now no Material overlay interferes
        colorPickerButton.background = gradientDrawable
        val colors = arrayOf(
            0xFFFF6F61.toInt(),
            0xFFFFD700.toInt(),
            0xFF20B2AA.toInt()
        )
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { anim ->
                val fraction = anim.animatedFraction
                val startColor = ArgbEvaluator().evaluate(fraction, colors[0], colors[1]) as Int
                val endColor = ArgbEvaluator().evaluate(fraction, colors[1], colors[2]) as Int
                gradientDrawable.colors = intArrayOf(startColor, endColor)
            }
        }
        animator.start()
    }
}