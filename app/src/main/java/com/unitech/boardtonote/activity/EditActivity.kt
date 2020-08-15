package com.unitech.boardtonote.activity

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.BlockAdapter
import com.unitech.boardtonote.data.BtnCloud
import com.unitech.boardtonote.data.BtnInterface
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.databinding.ActivityEditBinding
import com.unitech.boardtonote.fragment.BlockListFragment
import com.unitech.boardtonote.helper.SnackBarInterface
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_edit.*
import java.io.File

class EditActivity : AppCompatActivity(), SnackBarInterface {
    private val tag = "EditActivity"

    private lateinit var b: ActivityEditBinding

    lateinit var btnClass: BtnInterface
    lateinit var blockAdapter: BlockAdapter

    private var editMenu: Menu? = null

    private val startCropActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            RESULT_OK -> {
                val size = Point()
                windowManager.defaultDisplay.getSize(size)
                Glide.with(Image_OriPic).load(btnClass.oriPic).centerInside().into(Image_OriPic)
                onImageChange()
            }
            RESULT_CANCELED -> snackBar("User canceled cropping picture")
            else -> snackBar("Error raised while cropping picture")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        b = ActivityEditBinding.inflate(layoutInflater)

        setSupportActionBar(b.ToolbarEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val dirName = intent.getStringExtra("dirName")
        val location = intent.getIntExtra("location", 0)

        if (dirName == null || location == 0) {
            Log.e(tag, "dirName does not exist $dirName")
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("snackBar", "An Error Occurred : file does not exist.")
            startActivity(mainIntent)
        }

        btnClass = when (location) {
            Constant.locationLocal -> BtnLocal(this, dirName)
            Constant.locationCloud -> BtnCloud(this, dirName)
            else -> throw IllegalArgumentException()
        }
        b.EditTitle.setText(btnClass.dirName)
        b.EditTitle.setOnKeyListener { _, code, event ->
            if (event.action == KeyEvent.ACTION_DOWN && code == KeyEvent.KEYCODE_ENTER) {
                val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(b.EditTitle.windowToken, 0)
                val success = btnClass.rename(b.EditTitle.text.toString())
                if (success) {
                    true
                } else {
                    snackBar("Fail to rename note")
                    b.EditTitle.setText(btnClass.dirName)
                    false
                }
            } else {
                false
            }
        }

        setContentView(b.root)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.Frame_Edit, BlockListFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun onImageChange() {
        AlertDialog.Builder(this).apply {
            setTitle("Image Change Detected")
            setMessage("Do you want to update content?")
            setPositiveButton("Update") { dialogInterface, _ ->
                btnClass.analyze({ onSuccess() }, { onFailure() })
                dialogInterface.dismiss()
            }
            setNegativeButton("Keep Current") { dialogInterface, _ -> dialogInterface.dismiss() }
            show()
        }
    }

    private fun onSuccess(): Boolean {
        blockAdapter.notifyDataSetChanged()
        snackBar("Content Updated")
        return true
    }

    private fun onFailure(): Boolean {
        snackBar("An error occurred. Can't analyze picture.")
        return false
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        editMenu = menu
        showLocationAndState(btnClass.location, btnClass.state)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Menu_Share -> {
                btnClass.share(Constant.sharePdf)
                true
            }
            R.id.Menu_Crop -> {
                val options = UCrop.Options()
                options.apply {
                    setStatusBarColor(ContextCompat.getColor(this@EditActivity, R.color.primaryDark))
                    setToolbarColor(ContextCompat.getColor(this@EditActivity, R.color.accent))
                    setToolbarWidgetColor(ContextCompat.getColor(this@EditActivity, R.color.dark))
                }

                val ucropIntent = UCrop.of(Uri.fromFile(File(btnClass.oriPicPath)), Uri.fromFile(File(btnClass.oriPicPath)))
                        .withOptions(options)
                startCropActivity.launch(ucropIntent.getIntent(this@EditActivity))
                true
            }
            //When user pressed back button on toolbar
            android.R.id.home -> {
                return if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStackImmediate()
                    true
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showLocationAndState(location: Int, state: Int?): Boolean {
        val menu = editMenu?.findItem(R.id.Menu_LocationState) ?: return false
        when (location) {
            Constant.locationLocal -> menu.setIcon(R.drawable.ic_cloud_local_dark)
            Constant.locationCloud -> {
                when (state) {
                    Constant.stateSync -> menu.setIcon(R.drawable.ic_cloud_dark)
                    Constant.stateDownload -> menu.setIcon(R.drawable.ic_cloud_download_dark)
                    Constant.stateUpload -> menu.setIcon(R.drawable.ic_cloud_upload_dark)
                    else -> menu.setIcon(R.drawable.ic_error_dark)
                }
            }
        }
        return true
    }

    override fun snackBar(m: String) {
        Snackbar.make(b.CoorEdit, m, Snackbar.LENGTH_SHORT).show()
    }
}