package com.unitech.boardtonote.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.SettingsActivity
import com.unitech.boardtonote.helper.ThemeHelper

class RootFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preferences_root, rootKey)

        val themePreference = findPreference<ListPreference>("Preference_Theme")
        themePreference!!.value = ThemeHelper.loadTheme(requireActivity())
        themePreference.setOnPreferenceChangeListener { _, newValue ->
            val themeOption = newValue as String
            ThemeHelper.applyTheme(themeOption)
            ThemeHelper.saveTheme(requireActivity(), themeOption)
            Log.v(tag, "Theme : $themeOption")
            true
        }

        val dev = findPreference<Preference>("Preference_Dev")
        dev!!.setOnPreferenceClickListener {
            val container = LinearLayout(activity)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(48, 48, 48, 48)
            val edit = EditText(activity)
            edit.layoutParams = lp
            edit.requestFocus()
            container.addView(edit)

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edit, 0)
            edit.setOnKeyListener { _, key, event ->
                if (event.action == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_ENTER)
                {
                    imm.hideSoftInputFromWindow(edit.windowToken, 0)
                }
                true
            }

            AlertDialog.Builder(activity as Context).apply {
                val linearSettings = (requireActivity() as SettingsActivity).binding.LinearSettings
                setTitle("Enter Password")
                setView(container)
                setPositiveButton("Unlock") { _, _ ->
                    if (edit.text.toString() == "unitech") {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.Frame_Settings, DevFragment())
                            .addToBackStack(null)
                            .commit()
                        Snackbar.make(
                            linearSettings,
                            "You are now developer!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(linearSettings, "Password is wrong", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
                setNegativeButton("Cancel") { _, _ ->
                    Snackbar.make(
                        linearSettings,
                        "User has canceled entering Developer Options",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }.show()
            true
        }
    }
}