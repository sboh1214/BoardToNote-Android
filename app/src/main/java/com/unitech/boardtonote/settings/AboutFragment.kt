package com.unitech.boardtonote.settings

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.unitech.boardtonote.R

class AboutFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val info = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
        val versionName: Preference? = findPreference("Preference_VersionName")
        versionName?.title = info.versionName
        val versionCode: Preference? = findPreference("Preference_VersionCode")
        if (Build.VERSION.SDK_INT >= 28)
        {
            versionCode?.title = info.longVersionCode.toString()
        }
        else
        {
            @Suppress("DEPRECATION")
            versionCode?.title = info.versionCode.toString()
        }

        val btnLicense = findPreference<Preference>("Preference_BTNLicense")
        btnLicense!!.setOnPreferenceClickListener {
            showWebView("Board to Note LICENSE")
            true
        }
        val btnTos = findPreference<Preference>("Preference_BTNTos")
        btnTos!!.setOnPreferenceClickListener {
            showWebView("Board to Note Terms of Service")
            true
        }
        val tpLicense = findPreference<Preference>("Preference_TPLicense")
        tpLicense!!.setOnPreferenceClickListener {
            showWebView("Third-party LICENSE")
            true
        }
        val tpTos = findPreference<Preference>("Preference_TPTos")
        tpTos!!.setOnPreferenceClickListener {
            showWebView("Third-party TERMS OF SERVICE")
            true
        }
    }

    private fun showWebView(name: String) {
        AlertDialog.Builder(requireActivity()).apply {
            val webView = WebView(requireActivity())
            webView.loadUrl("file:///android_asset/$name/index.html")
            setView(webView)
            setPositiveButton("Dismiss") { dialogInterface, _ -> dialogInterface.dismiss() }
            show()
        }
    }
}