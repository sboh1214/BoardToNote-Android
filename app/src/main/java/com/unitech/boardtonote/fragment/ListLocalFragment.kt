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
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.activity.MainActivity
import com.unitech.boardtonote.adapter.ListLocalAdapter
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.data.BtnLocalList
import com.unitech.boardtonote.databinding.FragmentLocalBinding

class ListLocalFragment : Fragment() {
    private lateinit var mA: MainActivity
    private lateinit var b: FragmentLocalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mA = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        b = FragmentLocalBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mA.localAdapter = ListLocalAdapter(BtnLocalList(requireActivity()),
                { btnClass -> itemClick(btnClass) },
                { btnClass -> itemMoreClick(btnClass) })

        val metrics = DisplayMetrics()
        mA.windowManager.defaultDisplay.getMetrics(metrics)
        val dp: Int = metrics.widthPixels / (metrics.densityDpi / 180)

        b.RecyclerLocal.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(dp / 270, StaggeredGridLayoutManager.VERTICAL)
            adapter = mA.localAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun itemClick(btn: BtnLocal) {
        val intent = Intent(activity, EditActivity::class.java)
        intent.putExtra("dirName", btn.dirName)
        intent.putExtra("location", Constant.locationLocal)
        startActivity(intent)
        return
    }

    private fun itemMoreClick(btn: BtnLocal): Boolean {
        val fragment = BottomLocalFragment(btn)
        fragment.show(requireActivity().supportFragmentManager, "bottom_local")
        return true
    }
}