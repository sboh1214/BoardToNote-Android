package com.unitech.boardtonote.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.unitech.boardtonote.R
import com.unitech.boardtonote.settings.RootFragment
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    private val titleTag = "settingsActivityTitle"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
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
        setSupportActionBar(Toolbar_Preferences)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        }
        else
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
    }

    override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat,
            pref: Preference
    ): Boolean
    {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.Frame_Settings, fragment)
                .addToBackStack(null)
                .commit()
        title = pref.title
        return true
    }
}

