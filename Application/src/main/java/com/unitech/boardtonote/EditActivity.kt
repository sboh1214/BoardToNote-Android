package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*

private const val TAG = "EditActivity"

class EditActivity : AppCompatActivity()
{

    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        setContentView(R.layout.activity_edit)
        setSupportActionBar(Toolbar_Edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val dirName = intent.getStringExtra("dirName")
        if (dirName == null)
        {
            Log.e(TAG, "dirName does not exist")
            Snackbar.make(Linear_Edit, "An Error Occurred : file name does not exist.", Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnClass = BTNClass(this, dirName, BTNClass.Location.LOCAL)
        try
        {
            Edit_Title.setText(btnClass.dirName)
            Edit_Title.setOnKeyListener { view, code, event ->
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
                        Snackbar.make(Linear_Edit,"Fail to rename note",Snackbar.LENGTH_SHORT).show()
                        Edit_Title.setText(btnClass.dirName)
                        false
                    }
                }
                else
                {
                    false
                }
            }
            pictureView.setImageBitmap(btnClass.oriPic)
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Can't open dirName : $dirName")
            Snackbar.make(Linear_Edit, "An Error Occurred : Can't open note.", Snackbar.LENGTH_SHORT).show()
        }
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
            R.id.Menu_Save    ->
            {
                true
            }
            R.id.Menu_Share   ->
            {
                true
            }
            /**
             * When user pressed back button on toolbar
             */
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
