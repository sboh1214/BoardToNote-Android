package com.unitech.boardtonote.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.adapter.ListLocalAdapter
import com.unitech.boardtonote.data.BTNLocalClass
import com.unitech.boardtonote.data.ListLocalClass
import kotlinx.android.synthetic.main.fragment_list.*

class ListLocalFragment : Fragment()
{
    private lateinit var localAdapter: ListLocalAdapter
    private lateinit var localManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        localManager = GridLayoutManager(activity as Context, 2)
        localAdapter = ListLocalAdapter(ListLocalClass(activity as Context),
                { btnClass -> itemClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        Recycler_Main.apply {
            setHasFixedSize(true)
            layoutManager = localManager
            adapter = localAdapter
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
        val fragment = BottomLocalFragment(localAdapter, btnClass)
        fragment.show(activity!!.supportFragmentManager, "bottom_list")
        return true
    }
}