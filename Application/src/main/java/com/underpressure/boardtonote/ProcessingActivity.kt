package com.underpressure.boardtonote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import java.io.File
import java.sql.Timestamp

class ProcessingActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_OPEN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val prevIntent = intent
        val string = prevIntent.getStringExtra("URI")
        if (string == null) {
            val imageIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(imageIntent, REQUEST_IMAGE_OPEN)
        } else {
            val uri = Uri.parse(string)
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("URI", copyFile(uri))
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK) {
            val uri: Uri = data!!.data
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("URI", copyFile(uri))
            startActivity(intent)
        } else {
            Toast.makeText(this, "User has canceled opening picture.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun copyFile(originalUri: Uri): String? {
        try {
            val originalFile = File(originalUri.path)
            val time = System.currentTimeMillis().toInt()
            val ts = Timestamp(time.toLong()).toString()
            val newFile = File(this.filesDir, "/{BTN_$ts}/OriPic")
            originalFile.copyTo(newFile)
            return Uri.fromFile(newFile).toString()
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
            Toast.makeText(this, "Can't open picture.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return null
        }
    }
}
