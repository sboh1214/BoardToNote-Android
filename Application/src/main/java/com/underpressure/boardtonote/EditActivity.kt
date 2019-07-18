package com.underpressure.boardtonote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*


class EditActivity : AppCompatActivity() {

    private lateinit var btnClass: BTNClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "EditActivity")

        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        val intent = intent
        val dirName = intent.getStringExtra("DirName")
        if (dirName == null) {
            toast(this, "An Error Occurred : pictureUri does not exist.")
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            btnClass = BTNClass(this, dirName)
            try {
                pictureView.setImageBitmap(btnClass.OriginalPicture)
            } catch (e: Exception) {
                toast(this, "An Error Occurred : Can't open Picture.")
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
            else -> super.onOptionsItemSelected(item)
        }
    }

}
