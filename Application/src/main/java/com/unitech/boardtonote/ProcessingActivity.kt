package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_processing.*
import kotlinx.android.synthetic.main.fragment_processing_options.view.*


private const val TAG = "ProcessingActivity"

class ProcessingActivity : AppCompatActivity(), ProcessingOptionsFragment.AnalyzeListener
{
    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_processing)
        setSupportActionBar(Toolbar_Processing)
        supportFragmentManager.beginTransaction()
                .replace(R.id.Frame_Processing, ProcessingOptionsFragment())
                .commit()

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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data?.data != null)
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

    override fun analyze(): Boolean
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
                Log.i(TAG, "analyze() Success ${btnClass.dirName}")
                Log.v(TAG, firebaseVisionText.text)
                val intent = Intent(this@ProcessingActivity, EditActivity::class.java)
                intent.putExtra("dirName", btnClass.dirName)
                startActivity(intent)
            }
            addOnFailureListener { e ->
                Log.i(TAG, "analyze() Failure ${btnClass.dirName}")
                Log.w(TAG, e.toString())
            }
        }
        return true
    }

    companion object
    {
        private const val REQUEST_IMAGE_GET = 1
    }
}

class ProcessingOptionsFragment : Fragment()
{
    private lateinit var analyzeListener: AnalyzeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_processing_options, container, false)
        view.Button_Process.setOnClickListener { analyzeListener.analyze() }
        return view
    }

    override fun onAttach(context: Context)
    {
        if (context is AnalyzeListener)
        {
            analyzeListener = context
        }
        super.onAttach(context)
    }

    interface AnalyzeListener
    {
        fun analyze():Boolean
    }
}

class ProcessingImageFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_processing_image, container, false)
    }
}