package com.underpressure.boardtonote

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*


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
//                    // 선택한 이미지에서 비트맵 생성
//                    val in:InputStream = getContentResolver().openInputStream(data.getData());
//                    val img:Bitmap = BitmapFactory.decodeStream(in);
//                    in.close();
                        // 이미지 표시
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
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
