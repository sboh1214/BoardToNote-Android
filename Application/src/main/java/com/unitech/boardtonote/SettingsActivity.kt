package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File


private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{

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
            title = savedInstanceState.getCharSequence(TITLE_TAG)
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
        outState.putCharSequence(TITLE_TAG, title)
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

    class RootFragment : PreferenceFragmentCompat()
    {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
        {
            setPreferencesFromResource(R.xml.preferences_root, rootKey)

            val dev = findPreference<Preference>("Preference_Dev")
            dev!!.setOnPreferenceClickListener { _ ->
                val container = LinearLayout(activity)
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(48, 48, 48, 48)
                val edit = EditText(activity)
                edit.layoutParams = lp
                container.addView(edit)

                AlertDialog.Builder(activity as Context).apply {
                    setTitle("Enter Password")
                    setView(container)
                    setPositiveButton("Unlock") { _, _ ->
                        if (edit.text.toString() == "unitech")
                        {
                            activity!!.supportFragmentManager.beginTransaction()
                                    .replace(R.id.Frame_Settings, DevFragment())
                                    .addToBackStack(null)
                                    .commit()
                            Snackbar.make(activity!!.Linear_Settings, "You are now developer!", Snackbar.LENGTH_SHORT).show()
                        }
                        else
                        {
                            Snackbar.make(activity!!.Linear_Settings, "Password is wrong", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    setNegativeButton("Cancel") { _, _ ->
                        Snackbar.make(activity!!.Linear_Settings, "User has canceled entering Developer Options", Snackbar.LENGTH_SHORT).show()
                    }
                }.show()
                true
            }
        }
    }

    class ImageFragment : PreferenceFragmentCompat()
    {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
        {
            setPreferencesFromResource(R.xml.preference_image, rootKey)
        }
    }

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
}
