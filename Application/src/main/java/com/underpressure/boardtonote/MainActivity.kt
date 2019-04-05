package com.underpressure.boardtonote

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Camera_Fab.setOnClickListener {
            val nextIntent = Intent(this, CameraActivity::class.java)
            startActivity(nextIntent)
        }

        Gallery_Fab.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val input: InputStream = contentResolver.openInputStream(data?.data)
                        val img: Bitmap = BitmapFactory.decodeStream(input)
                        input.close()
                        val fileName = createTempPicture(this, img)
                        val intent = Intent(this, EditActivity::class.java)
                        intent.putExtra("FILE_NAME", fileName)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun createTempPicture(context: Context, bitmap: Bitmap): String {
        val storage = context.cacheDir
        val fileName = Timestamp(System.currentTimeMillis()).toString() + Random().nextInt(1000).toString() + ".png"
        val tempFile = File(storage, fileName)
        try {
            tempFile.createNewFile()
            val out = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close() // 마무리로 닫아줍니다.

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile.absolutePath
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
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
