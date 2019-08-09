package com.unitech.boardtonote.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.unitech.boardtonote.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File

class DevFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preference_dev, rootKey)

        val delete: Preference? = findPreference<Preference>("Preference_Delete")
        delete!!.setOnPreferenceClickListener {
            val dir = File(activity!!.filesDir!!.path)
            dir.deleteRecursively()
            Snackbar.make(activity!!.Linear_Settings, "Deleted all files", Snackbar.LENGTH_SHORT).show()
            true
        }

        val crash: Preference? = findPreference<Preference>("Preference_Crash")
        crash!!.setOnPreferenceClickListener {
            Crashlytics.getInstance().crash()
            true
        }
    }
}