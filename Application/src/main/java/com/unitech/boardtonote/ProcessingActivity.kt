package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_processing.*


private const val TAG = "ProcessingActivity"

class ProcessingActivity : AppCompatActivity()
{
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
                startActivityForResult(intent, Companion.REQUEST_IMAGE_GET)
            }
        }
        else
        {
            btnClass = BTNClass(this, dirName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == Companion.REQUEST_IMAGE_GET && resultCode == RESULT_OK && data?.data != null)
        {
            val uri: Uri = data.data!!
            btnClass = BTNClass(this as Context, BTNClass.makeDir(this as Context, null))
            btnClass.copyOriPic(uri)
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

    private fun analyze(): Boolean
    {
        if (btnClass.oriPic == null)
        {
            return false
        }
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(btnClass.oriPic!!)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image).apply {
            addOnSuccessListener { firebaseVisionText ->
                btnClass.visionText = firebaseVisionText
                Log.i(TAG, firebaseVisionText.text)
                val intent = Intent(this@ProcessingActivity, EditActivity::class.java)
                intent.putExtra("dirName", btnClass.dirName)
                startActivity(intent)
            }
            addOnFailureListener { e ->
                Log.e(TAG, e.toString())
            }
        }
        return true
    }

    companion object
    {
        private const val REQUEST_IMAGE_GET = 1
    }
}
