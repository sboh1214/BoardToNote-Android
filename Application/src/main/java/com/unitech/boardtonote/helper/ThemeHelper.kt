package com.unitech.boardtonote.helper

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

object ThemeHelper
{
    private const val NAME = "Theme"

    private const val LIGHT_MODE = "light"
    private const val DARK_MODE = "dark"
    private const val DEFAULT_MODE = "default"

    @JvmStatic
    fun applyTheme(themePref: String)
    {
        when (themePref)
        {
            LIGHT_MODE   ->
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DARK_MODE    ->
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            DEFAULT_MODE ->
            {
                if (Build.VERSION.SDK_INT >= 29)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    @JvmStatic
    fun saveTheme(context: Context, theme: String)
    {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().putString(NAME, theme).apply()
    }

    @JvmStatic
    fun loadTheme(context: Context): String
    {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(NAME, DEFAULT_MODE)!!
    }
}
