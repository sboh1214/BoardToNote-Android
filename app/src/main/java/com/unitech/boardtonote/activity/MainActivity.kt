package com.unitech.boardtonote.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.ListCloudAdapter
import com.unitech.boardtonote.adapter.ListLocalAdapter
import com.unitech.boardtonote.adapter.MainPagerAdapter
import com.unitech.boardtonote.data.BtnCloud
import com.unitech.boardtonote.data.BtnInterface
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.databinding.ActivityMainBinding
import com.unitech.boardtonote.fragment.AccountDialog
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.SnackBarInterface


class MainActivity : AppCompatActivity(), SnackBarInterface, AccountHelper.AccountInterface {
    private val tag = "MainActivity"

    private lateinit var b: ActivityMainBinding

    private var time: Long = 0
    private var mainMenu: Menu? = null

    lateinit var localAdapter: ListLocalAdapter
    lateinit var cloudAdapter: ListCloudAdapter

    private val startActivityForResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            val btnClass: BtnInterface =
                    when (b.pager.currentItem) {
                        0 -> {
                            intent.putExtra("location", Constant.locationLocal)
                            BtnLocal(this@MainActivity, null)
                        }
                        1 -> {
                            intent.putExtra("location", Constant.locationCloud)
                            BtnCloud(this@MainActivity, null)
                        }
                        else -> {
                            intent.putExtra("location", Constant.locationLocal)
                            BtnLocal(this@MainActivity, null)
                        }
                    }
            btnClass.copyOriPic(uri)
            intent.putExtra("dirName", btnClass.dirName)
            startActivity(intent)
        }
    }

    private val signInActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val response = IdpResponse.fromResultIntent(it.data)

        if (it.resultCode == RESULT_OK) {
            onSignIn()
            b.pager.adapter = MainPagerAdapter(this@MainActivity)
        } else {
            Log.e(tag, response?.error.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")

        b = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(b.ToolbarMain)
        b.pager.adapter = MainPagerAdapter(this@MainActivity)
        TabLayoutMediator(b.TabMain, b.pager) { tab, position ->
            tab.text = when (position) {
                0 -> "Local"
                1 -> "Cloud"
                else -> "Error"
            }
        }.attach()

        val message = intent.getStringExtra("snackBar")
        if (message != null && message != "") {
            snackBar(message)
        }

        when (intent.action) {
            "shortcut.local" -> {
                Log.i(tag, "shortcut.local")
                b.pager.currentItem = 0
            }
            "shortcut.cloud" -> {
                Log.i(tag, "shortcut.cloud")
                b.pager.currentItem = 1
            }
            else -> {
                Log.i(tag, "not shortcut")
                b.pager.currentItem = 0
            }
        }

        b.CameraFab.setOnClickListener {
            val location: Int = b.pager.currentItem
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("location", location)
            startActivity(intent)
        }

        b.GalleryFab.setOnClickListener {
            startActivityForResult.launch("image/*")
        }

        setContentView(b.root)
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() <= time + 2000) {
            Log.v(tag, "Press Back Button 2 time $time")
            Log.i(tag, "Exit Application.")
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            Log.v(tag, "Press Back Button 1 time $time")
            time = System.currentTimeMillis()
            snackBar("Press back one more time to exit.")
            return
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mainMenu = menu
        val searchView: SearchView = menu?.findItem(R.id.Menu_Search)?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.main_search_hint)
        showProfile()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Menu_Search -> {
                true
            }
            R.id.Menu_Account -> {
                val account = FirebaseAuth.getInstance().currentUser
                if (account == null) {
                    signInUI()
                } else {
                    onSignIn()
                }
                true
            }
            R.id.Menu_Setting -> {
                val intent = Intent(this as Context, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

    override fun requestSignIn() {
        signInUI()
    }

    override fun onSignIn() {
        super.onSignIn()
        showProfile()
        AccountDialog().show(supportFragmentManager, "accountDialog")
    }

    override fun onSignOut(context: Context, adapter: ListCloudAdapter) {
        super.onSignOut(context, adapter)
        mainMenu?.findItem(R.id.Menu_Account)?.setIcon(R.drawable.ic_account_dark)
    }

    private fun signInUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.BTN_ActionBar)
                .build()
        signInActivity.launch(intent)
    }

    private fun showProfile() {
        Glide.with(this)
                .load(AccountHelper.photoUrl)
                .apply(RequestOptions().circleCrop())
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                    ) {
                        mainMenu?.findItem(R.id.Menu_Account)?.icon = resource
                    }
                })
    }

    override fun snackBar(m: String) {
        Snackbar.make(b.CoorMain, m, Snackbar.LENGTH_SHORT).setAnchorView(b.LinearFloating).show()
    }
}
