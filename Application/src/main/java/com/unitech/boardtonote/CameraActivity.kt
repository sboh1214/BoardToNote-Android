package com.unitech.boardtonote

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

private const val TAG = "CameraActivity"

class CameraActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        // Initializes Fabric for builds that don't use the debug build type.
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()

        Fabric.with(this, crashlyticsKit)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        val theme = sharedPreferences.getString("Preference_Theme", "Default")
        applyTheme(theme!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, s ->
            val theme = sharedPreferences.getString(s, "Default").toString()
            applyTheme(theme)
            Log.i(TAG, "Applying theme to $theme")
        }

        if (Build.VERSION.SDK_INT < 21)
        {
            Log.w(TAG, "Android SDK : " + Build.VERSION.SDK_INT.toString())
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("snackBar", "This android version is not supported for camera preview.")
            startActivity(intent)
            return
        }
        setContentView(R.layout.activity_camera)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, CameraFragment.newInstance())
                .commit()
    }

    private fun applyTheme(theme: String)
    {
        when (theme)
        {
            "Light"   ->
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "Dark"    ->
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "Default" ->
            {
                if (BuildCompat.isAtLeastQ())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == requestImageGet && resultCode == RESULT_OK && data != null)
        {
            val uri = data.data!!
            val btnClass = BTNClass(this@CameraActivity, null, BTNClass.Location.LOCAL)
            btnClass.copyOriPic(uri)
            val intent = Intent(this@CameraActivity, EditActivity::class.java)
            intent.putExtra("dirName", btnClass.dirName)
            intent.putExtra("location", btnClass.location.value)
            startActivity(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
