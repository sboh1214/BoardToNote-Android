package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

private const val TAG = "ProcessingActivity"

class ProcessingActivity : AppCompatActivity() {

    private val requestImageOpen = 1

    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate")

        setContentView(R.layout.activity_processing)

        val prevIntent = intent
        val dirName = prevIntent.getStringExtra("dirName")

        if (dirName == null) {
            val imageIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(imageIntent, requestImageOpen)
        } else {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("dirName", dirName)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestImageOpen && resultCode == RESULT_OK)
        {
            val uri: Uri = data!!.data!!
            val intent = Intent(this, EditActivity::class.java)
            val file = File(uri.path as String)
            btnClass = BTNClass(this as Context, BTNClass.makeDir(this as Context, file.name))
            intent.putExtra("dirName", btnClass.dirName)
            startActivity(intent)
        } else {
            Toast.makeText(this, "User has canceled opening picture.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
