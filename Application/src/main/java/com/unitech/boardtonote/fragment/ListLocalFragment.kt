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
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.activity.MainActivity
import com.unitech.boardtonote.adapter.ListLocalAdapter
import com.unitech.boardtonote.data.BTNLocalClass
import com.unitech.boardtonote.data.ListLocalClass
import kotlinx.android.synthetic.main.fragment_local.*

class ListLocalFragment : Fragment()
{
    private lateinit var mA: MainActivity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        mA = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        mA.localAdapter = ListLocalAdapter(ListLocalClass(activity!!),
                { btnClass -> itemClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        val metrics = DisplayMetrics()
        mA.windowManager.defaultDisplay.getMetrics(metrics)
        val dp: Int = metrics.widthPixels / (metrics.densityDpi / 180)

        Recycler_Local.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(dp / 300, StaggeredGridLayoutManager.VERTICAL)
            adapter = mA.localAdapter
            itemAnimator = DefaultItemAnimator()
        }

        super.onActivityCreated(savedInstanceState)
    }

    private fun itemClick(btnClass: BTNLocalClass)
    {
        val intent = Intent(activity, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        startActivity(intent)
        return
    }

    private fun itemMoreClick(btnClass: BTNLocalClass): Boolean
    {
        val fragment = BottomLocalFragment(btnClass)
        fragment.show(activity!!.supportFragmentManager, "bottom_local")
        return true
    }
}