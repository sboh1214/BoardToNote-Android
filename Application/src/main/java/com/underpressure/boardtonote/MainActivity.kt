package com.underpressure.boardtonote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.io.File

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity()
{

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var btnList = arrayListOf<BTNClass>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        AppCenter.start(application, "15951c1d-dee3-4a12-8f06-e2a7b2d9ff35", Analytics::class.java, Crashes::class.java)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        getDirs(this)
        viewAdapter = BTNAdapter(btnList, { btnClass -> itemClick(btnClass) }, { btnClass -> itemLongClick(btnClass) })

        Recycler_Main.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        Camera_Fab.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        Gallery_Fab.setOnClickListener {
            val intent = Intent(this, ProcessingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun itemClick(btnClass: BTNClass)
    {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        startActivity(intent)
        return
    }

    private fun itemLongClick(btnClass: BTNClass): Boolean
    {
        return true
    }

    private fun getDirs(context: Context)
    {
        val dirList = File(context.filesDir.absolutePath).listFiles()
        if (dirList != null)
        {
            for (i in 0 until dirList.size)
            {
                if (dirList[i].isDirectory)
                {
                    btnList.add(BTNClass(context, dirList[i].name))
                }
            }
        }
    }

    override fun onBackPressed()
    {

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView: SearchView = menu?.findItem(R.id.Menu_Search)?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.main_search_hint)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        return when (item?.itemId)
        {
            R.id.Menu_Search ->
            {
                true
            }
            R.id.Menu_Setting ->
            {
                val intent = Intent(this as Context, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }

    }
}

class BTNAdapter(private val btnList: ArrayList<BTNClass>, private val itemClick: (BTNClass) -> Unit, private val itemLongClick: (BTNClass) -> Boolean) :
        RecyclerView.Adapter<BTNAdapter.BTNHolder>()
{
    inner class BTNHolder(item: View) : RecyclerView.ViewHolder(item)
    {
        fun bind(btnClass: BTNClass, itemClick: (BTNClass) -> Unit, itemLongClick: (BTNClass) -> Boolean)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.setOnLongClickListener { itemLongClick(btnClass) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BTNHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return BTNHolder(item)
    }

    override fun onBindViewHolder(holder: BTNHolder, position: Int)
    {
        holder.bind(btnList[position], itemClick, itemLongClick)
    }

    override fun getItemCount() = btnList.size
}