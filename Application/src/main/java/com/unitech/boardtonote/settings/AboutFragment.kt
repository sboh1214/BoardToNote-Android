package com.unitech.boardtonote.settings

import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.unitech.boardtonote.R

class AboutFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val info = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
        val versionName: Preference? = findPreference<Preference>("Preference_VersionName")
        versionName?.title = info.versionName
        val versionCode: Preference? = findPreference<Preference>("Preference_VersionCode")
        if (Build.VERSION.SDK_INT >= 28)
        {
            versionCode?.title = info.longVersionCode.toString()
        }
        else
        {
            @Suppress("DEPRECATION")
            versionCode?.title = info.versionCode.toString()
        }
    }
}