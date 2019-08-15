package com.unitech.boardtonote.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.adapter.ListCloudAdapter
import com.unitech.boardtonote.data.BTNCloudClass
import com.unitech.boardtonote.data.ListCloudClass
import kotlinx.android.synthetic.main.fragment_list.*

class ListCloudFragment : Fragment()
{
    private lateinit var cloudAdapter: ListCloudAdapter
    private lateinit var cloudManager: RecyclerView.LayoutManager
    private lateinit var cloudList: ListCloudClass

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        cloudList = ListCloudClass(activity as Context)
        cloudList.getDirListAsync {
            cloudManager = LinearLayoutManager(activity)
            cloudAdapter = ListCloudAdapter(ListCloudClass(activity as Context),
                    { btnClass -> itemClick(btnClass) },
                    { btnClass, _ -> itemMoreClick(btnClass) })

            Recycler_Main.apply {
                setHasFixedSize(true)
                layoutManager = cloudManager
                adapter = cloudAdapter
                itemAnimator = DefaultItemAnimator()
            }
            true
        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun itemClick(btnClass: BTNCloudClass)
    {
        val intent = Intent(activity, EditActivity::class.java)
        intent.putExtra("dirName", btnClass.dirName)
        startActivity(intent)
        return
    }

    private fun itemMoreClick(btnClass: BTNCloudClass): Boolean
    {
        val fragment = PopupCloudFragment(cloudAdapter, btnClass)
        fragment.show(activity!!.supportFragmentManager, "fragment_popup")
        return true
    }
}