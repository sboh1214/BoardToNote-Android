package com.unitech.boardtonote.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.unitech.boardtonote.R

class ImageFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preference_image, rootKey)
    }
}