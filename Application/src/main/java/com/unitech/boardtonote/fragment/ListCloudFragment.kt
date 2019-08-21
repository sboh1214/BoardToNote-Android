package com.unitech.boardtonote.fragment

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.activity.MainActivity
import com.unitech.boardtonote.adapter.ListCloudAdapter
import com.unitech.boardtonote.data.BTNCloudClass
import com.unitech.boardtonote.data.ListCloudClass
import com.unitech.boardtonote.helper.AccountHelper
import kotlinx.android.synthetic.main.fragment_cloud.*

class ListCloudFragment : Fragment()
{
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_cloud, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        if (AccountHelper.user == null)
        {
            return
        }
        mainActivity.cloudAdapter = ListCloudAdapter(ListCloudClass(activity!!),
                { btnClass -> itemClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        val metrics = DisplayMetrics()
        mainActivity.windowManager.defaultDisplay.getMetrics(metrics)
        val dp: Int = metrics.widthPixels / (metrics.densityDpi / 180)

        Recycler_Cloud.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(dp / 270, StaggeredGridLayoutManager.VERTICAL)
            adapter = mainActivity.cloudAdapter
            itemAnimator = DefaultItemAnimator()
        }

        mainActivity.cloudAdapter.listCloudClass.getDirListAsync {
            mainActivity.cloudAdapter.notifyDataSetChanged()
            true
        }
    }

    private fun itemClick(btnClass: BTNCloudClass)
    {
        val intent = Intent(activity, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        intent.putExtra("location", Constant.locationCloud)
        startActivity(intent)
        return
    }

    private fun itemMoreClick(btnClass: BTNCloudClass): Boolean
    {
        val fragment = BottomCloudFragment(btnClass)
        fragment.show(activity!!.supportFragmentManager, "bottom_local")
        return true
    }
}