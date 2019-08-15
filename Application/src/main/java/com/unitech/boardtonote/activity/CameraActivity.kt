package com.unitech.boardtonote.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.camera.CameraFragment
import com.unitech.boardtonote.data.BTNLocalClass

class CameraActivity : AppCompatActivity()
{
    private val tag = "CameraActivity"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        if (intent.action == "shortcut.take")
        {
            Log.i(tag, "shortcut.take")
        }
        if (intent.action == "shortcut.get")
        {
            Log.i(tag, "shortcut.get")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, Constant.requestImageGet)
        }

        if (Build.VERSION.SDK_INT < 21)
        {
            Log.w(tag, "Android SDK : " + Build.VERSION.SDK_INT.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == Constant.requestImageGet && resultCode == RESULT_OK && data != null)
        {
            val uri = data.data!!
            val btnClass = BTNLocalClass(this@CameraActivity, null)
            btnClass.copyOriPic(uri)
            val intent = Intent(this@CameraActivity, EditActivity::class.java)
            intent.putExtra("dirName", btnClass.dirName)
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
