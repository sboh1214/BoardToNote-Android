package com.unitech.boardtonote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*

private const val TAG = "EditActivity"

class EditActivity : AppCompatActivity() {

    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        setContentView(R.layout.activity_edit)
        setSupportActionBar(Toolbar_Edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val dirName = intent.getStringExtra("dirName")
        if (dirName == null) {
            Log.e(TAG,"dirName does not exist")
            Snackbar.make(Linear_Edit, "An Error Occurred : file name does not exist.", Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            btnClass = BTNClass(this, dirName)
            try {
                pictureView.setImageBitmap(btnClass.oriPic)
            } catch (e: Exception) {
                Log.e(TAG, "Can't open Picture dirName : $dirName")
                Snackbar.make(Linear_Edit, "An Error Occurred : Can't open Picture.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Menu_Save -> {
                true
            }
            R.id.Menu_Share -> {
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
            else -> super.onOptionsItemSelected(item)
        }
    }

}
