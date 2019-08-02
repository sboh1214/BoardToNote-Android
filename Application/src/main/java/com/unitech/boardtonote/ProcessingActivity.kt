package com.unitech.boardtonote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.perf.FirebasePerformance
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_processing.*
import kotlinx.android.synthetic.main.fragment_processing.view.*
import java.io.File
import java.lang.Exception


private const val TAG = "ProcessingActivity"

class ProcessingActivity : AppCompatActivity(), ProcessingFragment.ProcessingListener
{
    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_processing)
        setSupportActionBar(Toolbar_Processing)
        supportFragmentManager.beginTransaction()
                .replace(R.id.Frame_Processing, ProcessingFragment())
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
            btnClass = BTNClass(this, dirName,BTNClass.Location.LOCAL)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_IMAGE_GET)
        {
            if (resultCode == RESULT_OK && data?.data != null)
            {
                val uri: Uri = data.data!!
                btnClass = BTNClass(this as Context,null,BTNClass.Location.LOCAL)
                btnClass.copyOriPic(uri)
                Snackbar.make(Linear_Processing, "Created note from picture named ${btnClass.dirName}",Snackbar.LENGTH_SHORT).show()
            }
            else
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("snackBar","User has canceled opening picture")
                startActivity(intent)
            }
        }
        else if (requestCode == UCrop.REQUEST_CROP)
        {
            if (resultCode == RESULT_OK)
            {

            }
            else
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("snackBar","Error raised while cropping picture")
                startActivity(intent)
            }
        }
        else
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("snackBar","Unknown error raised")
            startActivity(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun crop():Boolean
    {
        UCrop.of(Uri.fromFile(File(btnClass.oriPicPath)), Uri.fromFile(File(btnClass.oriPicPath)))
                .start(this@ProcessingActivity)
        return true
    }

    override fun analyze(): Boolean
    {
        val onSuccess = {text:FirebaseVisionText->true}
        val onFailure = {e:Exception->true}
        btnClass.analyze(onSuccess, onFailure)
        return true
    }

    companion object
    {
        private const val REQUEST_IMAGE_GET = 1
    }
}

class ProcessingFragment : Fragment()
{
    private lateinit var processingListener: ProcessingListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_processing, container, false)
        view.Button_Process.setOnClickListener { processingListener.analyze() }
        view.Button_Crop.setOnClickListener {processingListener.crop()}
        return view
    }

    override fun onAttach(context: Context)
    {
        if (context is ProcessingListener)
        {
            processingListener = context
        }
        super.onAttach(context)
    }

    interface ProcessingListener
    {
        fun crop():Boolean
        fun analyze():Boolean
    }
}