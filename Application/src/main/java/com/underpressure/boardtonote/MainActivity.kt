package com.underpressure.boardtonote

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val REQUEST_IMAGE_OPEN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Camera_Fab.setOnClickListener {
            val nextIntent = Intent(this, CameraActivity::class.java)
            startActivity(nextIntent)
        }

        Gallery_Fab.setOnClickListener {
            val intent = Intent(this, ProcessingActivity::class.java)
            startActivity(intent)
        }

        Log.e("TAG", "MainActivity")
    }

    override fun onBackPressed() {

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        var searchView: SearchView = menu?.findItem(R.id.Search_Menu)?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.main_search_hint)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.Search_Menu ->

                return true
            else -> return false
        }

    }
}
