package com.unitech.boardtonote

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.unitech.boardtonote.adapter.BlockAdapter
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*
import java.io.File

class EditActivity : AppCompatActivity()
{
    private val tag = "EditActivity"

    private lateinit var btnClass: BTNClass

    private lateinit var blockAdapter: RecyclerView.Adapter<*>
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
        val location = intent.getIntExtra("location", -1)
        if (dirName == null || location == -1)
        {
            Log.e(tag, "dirName does not exist $dirName $location")
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("snackBar", "An Error Occurred : file does not exist.")
            startActivity(mainIntent)
        }
        btnClass = BTNClass(this, dirName, BTNClass.toLocate(location))
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
            btnClass.asyncGetContent({ content -> onSuccess(content) }, { content -> onFailure(content) })
        }
        catch (e: Exception)
        {
            Log.e(tag, "Can't open dirName : $dirName $location $e}")
            Snackbar.make(Linear_Edit, "An Error Occurred : Can't open note.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onSuccess(content: BTNClass.ContentClass): Boolean
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

    private fun onFailure(content: BTNClass.ContentClass): Boolean
    {
        return true
    }

    private fun itemClick(btnClass: BTNClass.BlockClass)
    {
    }

    private fun itemLongClick(btnClass: BTNClass.BlockClass): Boolean
    {
        return true
    }

    private fun itemMoreClick(btnClass: BTNClass.BlockClass): Boolean
    {
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
            else
            {
                Snackbar.make(Coor_Edit, "Error raised while cropping picture", Snackbar.LENGTH_SHORT).show()
            }
        }
        else
        {
            Snackbar.make(Coor_Edit, "Unknown error raised", Snackbar.LENGTH_SHORT).show()
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
                true
            }
            R.id.Menu_Crop    ->
            {
                UCrop.of(Uri.fromFile(File(btnClass.oriPicPath)), Uri.fromFile(File(btnClass.oriPicPath)))
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
}