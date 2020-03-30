package com.unitech.boardtonote.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.activity.MainActivity
import com.unitech.boardtonote.adapter.ListCloudAdapter
import com.unitech.boardtonote.data.BtnCloud
import com.unitech.boardtonote.data.BtnCloudList
import com.unitech.boardtonote.databinding.FragmentCloudBinding
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.SnackBarInterface

class ListCloudFragment : Fragment()
{
    private lateinit var mA: MainActivity
    private lateinit var b: FragmentCloudBinding
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        mA = activity as MainActivity
        snackBarInterface = activity as SnackBarInterface
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        b = FragmentCloudBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        if (AccountHelper.user == null)
        {
            return
        }
        mA.cloudAdapter = ListCloudAdapter(BtnCloudList(activity!!),
                { btnClass -> itemClick(btnClass) },
                { btnClass -> itemMoreClick(btnClass) })

        val metrics = DisplayMetrics()
        mA.windowManager.defaultDisplay.getMetrics(metrics)
        val dp: Int = metrics.widthPixels / (metrics.densityDpi / 180)

        b.RecyclerCloud.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(dp / 270, StaggeredGridLayoutManager.VERTICAL)
            adapter = mA.cloudAdapter
            itemAnimator = DefaultItemAnimator()
        }

        mA.cloudAdapter.btnCloudList.getDirListAsync {
            mA.cloudAdapter.notifyDataSetChanged()
            true
        }

        val cm = mA.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        val isWiFi: Boolean = activeNetwork?.type == ConnectivityManager.TYPE_WIFI
        if (!isConnected)
        {
            Log.d(tag, "No Internet Connection.")
            snackBarInterface.snackBar("There is not Internet Connection")
        }
        else
        {
            Log.d(tag, "Internet Connected")
        }
    }

    private fun itemClick(btn: BtnCloud)
    {
        val intent = Intent(activity, EditActivity::class.java)
        intent.putExtra("dirName", btn.dirName)
        intent.putExtra("location", Constant.locationCloud)
        startActivity(intent)
        return
    }

    private fun itemMoreClick(btn: BtnCloud): Boolean
    {
        val fragment = BottomCloudFragment(btn)
        fragment.show(activity!!.supportFragmentManager, "bottom_local")
        return true
    }
}