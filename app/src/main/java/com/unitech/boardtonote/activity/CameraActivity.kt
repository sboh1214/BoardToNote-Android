package com.unitech.boardtonote.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), LifecycleOwner {
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)

    private lateinit var b: ActivityCameraBinding

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                b.ViewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val btnClass = BtnLocal(this@CameraActivity, null)
                btnClass.copyOriPic(uri)
                Log.i(tag, btnClass.toString())
                val intent = Intent(this@CameraActivity, EditActivity::class.java)
                intent.putExtra("dirName", btnClass.dirName)
                intent.putExtra("location", Constant.locationLocal)
                startActivity(intent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        b = ActivityCameraBinding.inflate(layoutInflater)


        b.ButtonNote.setOnClickListener {
            val intent = Intent(this@CameraActivity, MainActivity::class.java)
            startActivity(intent)
        }
        b.ButtonGallery.setOnClickListener {
            startActivityForResult.launch("image/*")
        }
        b.ButtonPicture.setOnClickListener { takePhoto() }
        // Request camera permissions
        if (allPermissionsGranted()) {
            b.ViewFinder.post { startCamera() }
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        setContentView(b.root)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (window.insetsController != null) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            }
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            preview = Preview.Builder().build()
            // Select back camera
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                preview?.setSurfaceProvider(b.ViewFinder.createSurfaceProvider())
            } catch (exc: Exception) {
                Log.e(tag, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // Create timestamped output file to hold the image
        val btn = BtnLocal(this as Context, null)
        val photoFile = File(btn.oriPicPath)
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(tag, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(tag, msg)
                    Log.i(tag, "Captured picture with dirName ${btn.dirName}")
                    val intent = Intent(this@CameraActivity, EditActivity::class.java)
                    intent.putExtra("dirName", btn.dirName)
                    intent.putExtra("location", Constant.locationLocal)
                    startActivity(intent)
                }
            })
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val tag = "CameraActivity"
    }
}
