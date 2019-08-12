package com.unitech.boardtonote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.BTNAdapter
import com.unitech.boardtonote.adapter.MyLookup
import com.unitech.boardtonote.data.LocalBTNClass
import com.unitech.boardtonote.fragment.PopupFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity(), PopupFragment.PopupListener
{
    private val tag = "MainActivity"

    private lateinit var btnAdapter: RecyclerView.Adapter<BTNAdapter.BTNHolder>
    private lateinit var btnManager: RecyclerView.LayoutManager
    private var btnList = arrayListOf<LocalBTNClass>()
    private lateinit var tracker: SelectionTracker<Long>

    private var time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        setContentView(R.layout.activity_main)
        setSupportActionBar(Toolbar_Main)

        val snackBar = intent.getStringExtra("snackBar")
        if (snackBar != null && snackBar != "")
        {
            Snackbar.make(Linear_Main, snackBar, Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
        }

        btnManager = LinearLayoutManager(this)
        getDirs(this)
        btnAdapter = BTNAdapter(btnList,
                { btnClass -> itemClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        Recycler_Main.apply {
            setHasFixedSize(true)
            layoutManager = btnManager
            adapter = btnAdapter
        }

        tracker = SelectionTracker.Builder<Long>(
                "mySelection",
                Recycler_Main,
                StableIdKeyProvider(Recycler_Main),
                MyLookup(Recycler_Main),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build()

        (btnAdapter as BTNAdapter).tracker = tracker

        tracker.addObserver(
                object : SelectionTracker.SelectionObserver<Long>()
                {
                })

        Camera_Fab.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        Gallery_Fab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, Constant.requestImageGet)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == Constant.requestImageGet && resultCode == RESULT_OK && data != null)
        {
            val uri = data.data!!
            val btnClass = LocalBTNClass(this@MainActivity, null)
            btnClass.copyOriPic(uri)
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            intent.putExtra("dirName", btnClass.dirName)
            startActivity(intent)
        }
        else if (requestCode == Constant.requestSignIn)
        {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK)
            {
                val user = FirebaseAuth.getInstance().currentUser
                Log.i(tag, "firebase sign in success")
                Log.v(tag, "displayName : ${user?.displayName}")
                Log.v(tag, "email       : ${user?.email}")
                Log.v(tag, "uid         : ${user?.uid}")
                Log.v(tag, "photoUrl    : ${user?.photoUrl}")
                Snackbar.make(Linear_Main, "Welcome ${user?.displayName}!", Snackbar.LENGTH_SHORT).show()
            }
            else
            {
                Log.e(tag, response?.error.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed()
    {
        if (System.currentTimeMillis() <= time + 2000)
        {
            Log.v(tag, "Press Back Button 2 time $time")
            Log.i(tag, "Exit Application.")
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        else
        {
            Log.v(tag, "Press Back Button 1 time $time")
            time = System.currentTimeMillis()
            Snackbar.make(Linear_Main, "Press back on more time to exit.", Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
            return
        }
    }

    private fun itemClick(btnClass: LocalBTNClass)
    {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        startActivity(intent)
        return
    }

    private fun itemMoreClick(btnClass: LocalBTNClass): Boolean
    {
        val fragment = PopupFragment(btnClass)
        fragment.show(supportFragmentManager, "fragment_popup")
        return true
    }

    override fun rename(btnClass: LocalBTNClass)
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

    override fun delete(btnClass: LocalBTNClass)
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
                btnList.add(LocalBTNClass(context, dirName))
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
            R.id.Menu_Account ->
            {
                val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build())
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(), Constant.requestSignIn)
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