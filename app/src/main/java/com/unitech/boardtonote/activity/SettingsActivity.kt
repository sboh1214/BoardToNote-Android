package com.unitech.boardtonote.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.unitech.boardtonote.R
import com.unitech.boardtonote.databinding.ActivitySettingsBinding
import com.unitech.boardtonote.settings.RootFragment

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    private val titleTag = "settingsActivityTitle"

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        if (savedInstanceState == null)
        {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Frame_Settings, RootFragment())
                    .commit()
        }
        else
        {
            title = savedInstanceState.getCharSequence(titleTag)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
            {
                setTitle(R.string.title_activity_settings)
            }
        }
        setSupportActionBar(binding.ToolbarPreferences)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(titleTag, title)
    }

    override fun onSupportNavigateUp(): Boolean
    {
        return if (supportFragmentManager.popBackStackImmediate())
        {
            true
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference
    ): Boolean {
        val args: Bundle = pref.extras
        val fragment: Fragment =
            supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment)
        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.Frame_Settings, fragment)
            .addToBackStack(null).commit()
        title = pref.title

        return true
    }
}

