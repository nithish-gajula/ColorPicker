package com.example.colorpicker

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Inflate your preferences.xml file
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("pref_app_ver")?.summary = getString(R.string.version)
        findPreference<Preference>("pref_license")?.setOnPreferenceClickListener {
            openLicenseInBrowser()
            true
        }

        findPreference<Preference>("pref_rate_app")?.setOnPreferenceClickListener {
            showRateAppDialog()
            true
        }

        findPreference<Preference>("pref_app_ver")?.setOnPreferenceClickListener {
            showUpdateAppDialog()
            true
        }

    }

    private fun openLicenseInBrowser() {
        val intent = Intent(requireContext(), LicenseActivity::class.java)
        startActivity(intent)
    }

    private fun showRateAppDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.cac_dialog_for_rate_us)

        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(0.toDrawable())

        // Find views
        val tvAppName = dialog.findViewById<TextView>(R.id.tvAppName)
        val tvNo = dialog.findViewById<TextView>(R.id.tvNo)
        val tvRateNow = dialog.findViewById<TextView>(R.id.tvRateNow)

        tvAppName.clearAnimation()

        tvNo.setOnClickListener {
            dialog.dismiss()
        }

        tvRateNow.setOnClickListener {
            dialog.dismiss()
            // Open Play Store to rate app
            val intent = Intent(Intent.ACTION_VIEW, "market://details?id=${requireContext().packageName}".toUri())
            startActivity(intent)
        }

        dialog.show()
    }

    private fun showUpdateAppDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_app_update_check)

        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(0.toDrawable())  // Transparent background

        // Access views manually
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val tvAppName = dialog.findViewById<TextView>(R.id.tvAppName)
        val tvUpdateDesc = dialog.findViewById<TextView>(R.id.tvUpdateDesc)
        val tvUpdateNow = dialog.findViewById<TextView>(R.id.tvUpdateNow)

        // Optional: set values dynamically
        tvAppName.text = getString(R.string.app_name)
        tvUpdateDesc.text = getString(R.string.msg_check_for_update)

        // Close button
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        // "Update Now" button
        tvUpdateNow.setOnClickListener {
            dialog.dismiss()
            // Open Play Store to update app
            val intent = Intent(Intent.ACTION_VIEW, "market://details?id=${requireContext().packageName}".toUri())
            startActivity(intent)
        }

        dialog.show()
    }


}