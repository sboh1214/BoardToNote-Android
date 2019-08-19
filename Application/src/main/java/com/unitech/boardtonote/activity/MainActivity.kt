package com.unitech.boardtonote.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.MainPagerAdapter
import com.unitech.boardtonote.data.BTNCloudClass
import com.unitech.boardtonote.data.BTNInterface
import com.unitech.boardtonote.data.BTNLocalClass
import com.unitech.boardtonote.fragment.AccountDialog
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SnackBarInterface, AccountHelper.AccountInterface
{
    private val tag = "MainActivity"

    private var time: Long = 0
    private var mainMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        setContentView(R.layout.activity_main)
        setSupportActionBar(Toolbar_Main)

        val message = intent.getStringExtra("snackBar")
        if (message != null && message != "")
        {
            snackBar(message)
        }

        pager.adapter = MainPagerAdapter(supportFragmentManager)
        if (intent.action == "shortcut.local")
        {
            Log.i(tag, "shortcut.local")
            pager.currentItem = 0
        }
        else if (intent.action == "shortcut.cloud")
        {
            Log.i(tag, "shortcut.cloud")
            pager.currentItem = 1
        }
        else
        {
            pager.currentItem = 0
        }

        Camera_Fab.setOnClickListener {
            val location: Int = pager.currentItem
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("location", location)
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
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            val btnClass: BTNInterface =
                    when (pager.currentItem)
                    {
                        BTNInterface.Location.LOCAL.value ->
                        {
                            intent.putExtra("location", BTNInterface.Location.LOCAL.value)
                            BTNLocalClass(this@MainActivity, null)
                        }
                        BTNInterface.Location.CLOUD.value ->
                        {
                            intent.putExtra("location", BTNInterface.Location.CLOUD.value)
                            BTNCloudClass(this@MainActivity, null)
                        }
                        else                              ->
                        {
                            intent.putExtra("location", BTNInterface.Location.LOCAL.value)
                            BTNLocalClass(this@MainActivity, null)
                        }
                    }
            btnClass.copyOriPic(uri)
            intent.putExtra("dirName", btnClass.dirName)
            startActivity(intent)
        }
        else if (requestCode == Constant.requestSignIn)
        {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK)
            {
                onSignIn()
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
            snackBar("Press back one more time to exit.")
            return
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu_main, menu)
        mainMenu = menu
        val searchView: SearchView = menu?.findItem(R.id.Menu_Search)?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.main_search_hint)
        val url = FirebaseAuth.getInstance().currentUser?.photoUrl

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
                val account = FirebaseAuth.getInstance().currentUser
                if (account == null)
                {
                    signInUI()
                }
                else
                {
                    onSignIn()
                }
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

    override fun requestSignIn()
    {
        signInUI()
    }

    override fun onSignIn()
    {
        super.onSignIn()
        Glide.with(this)
                .load(AccountHelper.photoUrl)
                .apply(RequestOptions().circleCrop())
                .into(object : CustomTarget<Drawable>()
                {
                    override fun onLoadCleared(placeholder: Drawable?)
                    {

                    }

                    override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                    )
                    {
                        mainMenu?.findItem(R.id.Menu_Account)?.icon = resource
                    }
                })
        AccountDialog().show(supportFragmentManager, "accountDialog")
    }

    override fun onSignOut()
    {
        mainMenu?.findItem(R.id.Menu_Account)?.setIcon(R.drawable.ic_account_dark)
    }

    private fun signInUI()
    {
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher)
                        .setTheme(R.style.BTN_ActionBar)
                        .build(), Constant.requestSignIn)
    }

    override fun snackBar(m: String)
    {
        Snackbar.make(Coor_Main, m, Snackbar.LENGTH_SHORT).setAnchorView(Linear_Floating).show()
    }
}
