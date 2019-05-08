package com.underpressure.boardtonote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*


class EditActivity : AppCompatActivity() {

    var btnClass = BoardToNoteClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        val intent = intent
        val pictureUri = intent.getStringExtra("URI")
        if (pictureUri == null) {
            Toast.makeText(this, "An Error Occurred : pictureUri does not exist.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(pictureUri))
                btnClass.Picture = bitmap

                pictureView.setImageBitmap(btnClass.Picture)
            } catch (e: Exception) {
                Toast.makeText(this, "An Error Occurred : Can't open Picture.", Toast.LENGTH_SHORT).show()
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
        when (item.itemId) {
            R.id.Menu_Save -> {
                return true
            }
            R.id.Menu_Share -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
