package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_processing.*


private const val TAG = "ProcessingActivity"
private const val REQUEST_IMAGE_GET = 1

class ProcessingActivity : AppCompatActivity()
{

    private val requestImageOpen = 1

    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_processing)
        setSupportActionBar(Toolbar_Processing)

        val prevIntent = intent
        val dirName = prevIntent.getStringExtra("dirName")

        if (dirName == null)
        {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            if (intent.resolveActivity(packageManager) != null)
            {
                startActivityForResult(intent, REQUEST_IMAGE_GET)
            }
        }
        else
        {
            btnClass = BTNClass(this, dirName)
            analyze()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data?.data != null)
        {
            val uri: Uri = data.data!!
            btnClass = BTNClass(this as Context, BTNClass.makeDir(this as Context, null))
            btnClass.copyOriPic(uri)

            analyze()
        }
        else
        {
            Toast.makeText(this, "User has canceled opening picture.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun analyze()
    {
        btnClass.analyzePic()
        Text_Test.text = btnClass.visionText?.text
    }

    private fun startEditActivity()
    {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        startActivity(intent)
    }
}
