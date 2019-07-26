package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_popup.view.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.io.File


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), PopupFragment.PopupListener
{

    private lateinit var btnAdapter: RecyclerView.Adapter<*>
    private lateinit var btnManager: RecyclerView.LayoutManager
    private var btnList = arrayListOf<BTNClass>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        setContentView(R.layout.activity_main)
        setSupportActionBar(Toolbar_Main)

        btnManager = LinearLayoutManager(this)
        getDirs(this)
        btnAdapter = BTNAdapter(btnList, { btnClass -> itemClick(btnClass) }, { btnClass -> itemLongClick(btnClass) }, { btnClass, view -> itemMoreClick(btnClass, view) })

        Recycler_Main.apply {
            setHasFixedSize(true)
            layoutManager = btnManager
            adapter = btnAdapter
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

    private fun itemMoreClick(btnClass: BTNClass, view: View): Boolean
    {
        val fragment = PopupFragment(btnClass)
        fragment.show(supportFragmentManager, "fragment_popup")
        return true
    }

    override fun delete(btnClass: BTNClass)
    {
        (btnAdapter as BTNAdapter).delete(btnClass)
        Snackbar.make(Recycler_Main, "${btnClass.dirName} deleted", Snackbar.LENGTH_SHORT).show()
    }



    private fun getDirs(context: Context)
    {
        val dirList = File(context.filesDir.absolutePath).listFiles() ?: return
        for (i in 0 until dirList.size)
        {
            if (!dirList[i].isDirectory)
            {
                continue
            }
            if (dirList[i].name.substringAfterLast('.') == "btn")
            {
                btnList.add(BTNClass(context, dirList[i].name.substringBeforeLast('.')))
            }
        }
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

class BTNAdapter(private val btnList: ArrayList<BTNClass>,
                 private val itemClick: (BTNClass) -> Unit,
                 private val itemLongClick: (BTNClass) -> Boolean,
                 private val itemMoreClick: (BTNClass, View) -> Boolean) :
        RecyclerView.Adapter<BTNAdapter.BTNHolder>()
{
    inner class BTNHolder(item: View) : RecyclerView.ViewHolder(item)
    {
        fun bind(btnClass: BTNClass, itemClick: (BTNClass) -> Unit, itemLongClick: (BTNClass) -> Boolean, itemMoreClick: (BTNClass, View) -> Boolean)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.setOnLongClickListener { itemLongClick(btnClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(btnClass, itemView) }
        }
    }

    fun delete(btnClass: BTNClass)
    {
        btnClass.delete()
        btnList.remove(btnClass)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BTNHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return BTNHolder(item)
    }

    override fun onBindViewHolder(holder: BTNHolder, position: Int)
    {
        holder.bind(btnList[position], itemClick, itemLongClick, itemMoreClick)
    }

    override fun getItemCount() = btnList.size
}

class PopupFragment(private var btnClass: BTNClass) : BottomSheetDialogFragment()
{
    lateinit var popupListener: PopupListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)
        view.Button_Delete.setOnClickListener {
            popupListener.delete(btnClass)
            dismiss()
        }
        return view
    }

    override fun onAttach(context: Context)
    {
        if (context is PopupListener)
        {
            popupListener = context
        }
        super.onAttach(context)
    }

    interface PopupListener
    {
        fun delete(btnClass: BTNClass)
    }
}