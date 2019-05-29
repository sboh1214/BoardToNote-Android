package com.underpressure.boardtonote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.File

class ProcessingActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_OPEN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "ProcessingActivity")

        setContentView(R.layout.activity_gallery)

        val prevIntent = intent
        val dirName = prevIntent.getStringExtra("DirName")

        if (dirName == null) {
            val imageIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(imageIntent, REQUEST_IMAGE_OPEN)
        } else {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("DirName", dirName)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK) {
            val uri: Uri = data!!.data
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("DirName", copyFile(uri))
            startActivity(intent)
        } else {
            toast(this, "User has canceled opening picture.")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun copyFile(originalUri: Uri): String? {
        return try {
            val originalFile = File(originalUri.path)
            val dirName = makeDir(this, originalFile.name)
            val newFile = File(this.filesDir, "$dirName/OriPic.jpg")
            originalFile.copyTo(newFile)
            dirName
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
            toast(this, "Can't open picture.")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            null
        }
    }

    private fun File.copyTo(file: File) {
        inputStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
