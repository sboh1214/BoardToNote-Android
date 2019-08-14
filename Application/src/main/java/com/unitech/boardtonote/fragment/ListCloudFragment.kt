package com.unitech.boardtonote.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.MainPagerAdapter

class ListCloudFragment : Fragment()
{
    private lateinit var mainPagerAdapter: MainPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_list_cloud, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        mainPagerAdapter = MainPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = mainPagerAdapter
    }
}