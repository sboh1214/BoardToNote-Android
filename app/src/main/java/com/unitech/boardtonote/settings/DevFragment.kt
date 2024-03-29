package com.unitech.boardtonote.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.SettingsActivity
import java.io.File

class DevFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preference_dev, rootKey)

        val delete: Preference? = findPreference("Preference_Delete")
        delete!!.setOnPreferenceClickListener {
            val dir = File(requireActivity().filesDir!!.path)
            dir.deleteRecursively()
            Snackbar.make(
                (requireActivity() as SettingsActivity).binding.LinearSettings,
                "Deleted all files",
                Snackbar.LENGTH_SHORT
            ).show()
            true
        }

        val crash: Preference? = findPreference("Preference_Crash")
        crash!!.setOnPreferenceClickListener {
            throw RuntimeException("Test Crash")
        }
    }
}