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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.BlockAdapter
import com.unitech.boardtonote.data.BTNCloudClass
import com.unitech.boardtonote.data.BTNInterface
import com.unitech.boardtonote.data.BTNLocalClass
import com.unitech.boardtonote.fragment.BlockDialog
import com.unitech.boardtonote.fragment.BottomBlockFragment
import com.unitech.boardtonote.helper.SnackBarInterface
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*
import java.io.File

class EditActivity : AppCompatActivity(), SnackBarInterface
{
    private val tag = "EditActivity"

    lateinit var btnClass: BTNInterface

    lateinit var blockAdapter: BlockAdapter
    private lateinit var blockManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        setContentView(R.layout.activity_edit)
        setSupportActionBar(Toolbar_Edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val dirName = intent.getStringExtra("dirName")
        val location = intent.getIntExtra("location", 0)

        if (dirName == null)
        {
            Log.e(tag, "dirName does not exist $dirName")
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("snackBar", "An Error Occurred : file does not exist.")
            startActivity(mainIntent)
        }

        btnClass = when (location)
        {
            BTNInterface.Location.LOCAL.value -> BTNLocalClass(this, dirName)
            BTNInterface.Location.CLOUD.value -> BTNCloudClass(this, dirName)
            else                              -> throw IllegalArgumentException()
        }

        try
        {
            Edit_Title.setText(btnClass.dirName)
            Edit_Title.setOnKeyListener { _, code, event ->
                if (event.action == KeyEvent.ACTION_DOWN && code == KeyEvent.KEYCODE_ENTER)
                {
                    val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    input.hideSoftInputFromWindow(Edit_Title.windowToken, 0)
                    val success = btnClass.rename(Edit_Title.text.toString())
                    if (success)
                    {
                        true
                    }
                    else
                    {
                        Snackbar.make(Linear_Edit, "Fail to rename note", Snackbar.LENGTH_SHORT).show()
                        Edit_Title.setText(btnClass.dirName)
                        false
                    }
                }
                else
                {
                    false
                }
            }

            val size = Point()
            windowManager.defaultDisplay.getSize(size)
            pictureView.setImageBitmap(btnClass.decodeOriPic(size.x, null))
            btnClass.asyncGetContent({ content -> onSuccess(content) }, { onFailure() })
        }
        catch (e: Exception)
        {
            Log.e(tag, "Can't open dirName : $dirName $e}")
            Snackbar.make(Linear_Edit, "An Error Occurred : Can't open note.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onSuccess(content: BTNInterface.ContentClass): Boolean
    {
        Log.i(tag, "Recycler_Edit Init")
        blockManager = LinearLayoutManager(this)
        blockAdapter = BlockAdapter(btnClass.content.blockList,
                { btnClass -> itemClick(btnClass) },
                { btnClass -> itemLongClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        Recycler_Edit.apply {
            setHasFixedSize(true)
            layoutManager = blockManager
            adapter = blockAdapter
        }
        return true
    }

    private fun onFailure(): Boolean
    {
        return true
    }

    private fun itemClick(btnClass: BTNInterface.BlockClass)
    {
        BlockDialog().show(supportFragmentManager, "blockDialog")
        return
    }

    private fun itemLongClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        return true
    }

    private fun itemMoreClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        val fragment = BottomBlockFragment(btnClass)
        fragment.show(this@EditActivity.supportFragmentManager, "bottom_block")
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == UCrop.REQUEST_CROP)
        {
            if (resultCode == RESULT_OK)
            {
                val size = Point()
                windowManager.defaultDisplay.getSize(size)
                pictureView.setImageBitmap(btnClass.decodeOriPic(size.x, null))
            }
            else if (resultCode == RESULT_CANCELED)
            {
                snackBar("User canceled cropping picture")
            }
            else
            {
                snackBar("Error raised while cropping picture")
            }
        }
        else
        {
            snackBar("Unknown error raised")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.Menu_Share   ->
            {
                startActivity(btnClass.share(BTNInterface.Share.PDF))
                true
            }
            R.id.Menu_Crop    ->
            {
                val options = UCrop.Options()
                options.apply {
                    setStatusBarColor(ContextCompat.getColor(this@EditActivity, R.color.primaryDark))
                    setToolbarColor(ContextCompat.getColor(this@EditActivity, R.color.accent))
                    setToolbarWidgetColor(ContextCompat.getColor(this@EditActivity, R.color.dark))
                }

                UCrop.of(Uri.fromFile(File(btnClass.oriPicPath)), Uri.fromFile(File(btnClass.oriPicPath)))
                        .withOptions(options)
                        .start(this@EditActivity)
                true
            }
            //When user pressed back button on toolbar
            android.R.id.home ->
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else              -> super.onOptionsItemSelected(item)
        }
    }

    override fun snackBar(m: String)
    {
        Snackbar.make(Coor_Edit, m, Snackbar.LENGTH_SHORT).show()
    }
}