package com.unitech.boardtonote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
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

const val requestImageGet = 1

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), PopupFragment.PopupListener
{
    private lateinit var btnAdapter: RecyclerView.Adapter<*>
    private lateinit var btnManager: RecyclerView.LayoutManager
    private var btnList = arrayListOf<BTNClass>()

    private var time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        setContentView(R.layout.activity_main)
        setSupportActionBar(Toolbar_Main)

        val snackBar = intent.getStringExtra("snackBar")
        if (snackBar!=null && snackBar!="")
        {
            Snackbar.make(Linear_Main,snackBar,Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
        }

        btnManager = LinearLayoutManager(this)
        getDirs(this)
        btnAdapter = BTNAdapter(btnList, { btnClass -> itemClick(btnClass) },
                { btnClass -> itemLongClick(btnClass) }, { btnClass, _ -> itemMoreClick(btnClass) })

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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, requestImageGet)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == requestImageGet && resultCode == RESULT_OK && data != null)
        {
            val uri = data.data!!
            val btnClass = BTNClass(this@MainActivity, null, BTNClass.Location.LOCAL)
            btnClass.copyOriPic(uri)
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            intent.putExtra("dirName", btnClass.dirName)
            intent.putExtra("location", btnClass.location.value)
            startActivity(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed()
    {
        if (System.currentTimeMillis() <= time + 2000)
        {
            Log.v(TAG, "Press Back Button 2 time $time")
            Log.i(TAG, "Exit Application.")
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        else
        {
            Log.v(TAG, "Press Back Button 1 time $time")
            time = System.currentTimeMillis()
            Snackbar.make(Linear_Main, "Press back on more time to exit.", Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
            return
        }
    }

    private fun itemClick(btnClass: BTNClass)
    {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        intent.putExtra("location", btnClass.location.value)
        startActivity(intent)
        return
    }

    private fun itemLongClick(btnClass: BTNClass): Boolean
    {
        return true
    }

    private fun itemMoreClick(btnClass: BTNClass): Boolean
    {
        val fragment = PopupFragment(btnClass)
        fragment.show(supportFragmentManager, "fragment_popup")
        return true
    }

    override fun rename(btnClass: BTNClass)
    {
        val srcName = btnClass.dirName
        var dstName: String?

        val container = LinearLayout(this@MainActivity)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(48, 48, 48, 48)
        val edit = EditText(this@MainActivity)
        edit.layoutParams = lp
        container.addView(edit)

        AlertDialog.Builder(this).apply {
            setTitle("Rename Note")
            setView(container)
            setPositiveButton("Rename") { _, _ ->
                dstName = edit.text.toString()
                (btnAdapter as BTNAdapter).rename(btnClass, dstName!!)
                Snackbar.make(Linear_Main, "$srcName renamed to $dstName", Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
            }
            setNegativeButton("Cancel") { _, _ ->
                Snackbar.make(Linear_Main, "User canceled renaming $srcName", Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
            }
        }.show()
    }

    override fun delete(btnClass: BTNClass)
    {
        (btnAdapter as BTNAdapter).delete(btnClass)
        Snackbar.make(Linear_Main, "${btnClass.dirName} deleted", Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
    }

    private fun getDirs(context: Context)
    {
        val dirList = File(context.filesDir.absolutePath + "/local").listFiles() ?: return
        for (i in 0 until dirList.size)
        {
            if (!dirList[i].isDirectory)
            {
                continue
            }
            if (dirList[i].name.substringAfterLast('.') == "btn")
            {
                val dirName = dirList[i].name.substringBeforeLast('.')
                btnList.add(BTNClass(context, dirName ,BTNClass.Location.LOCAL))
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
            R.id.Menu_Search  ->
            {
                true
            }
            R.id.Menu_Setting ->
            {
                val intent = Intent(this as Context, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else              -> false
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

    fun rename(btnClass: BTNClass, name: String)
    {
        btnClass.rename(name)
        notifyDataSetChanged()
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
    private lateinit var popupListener: PopupListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)
        view.Text_Title.text = btnClass.dirName
        view.Button_Rename.setOnClickListener {
            popupListener.rename(btnClass)
            dismiss()
        }
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
        fun rename(btnClass: BTNClass)
        fun delete(btnClass: BTNClass)
    }
}