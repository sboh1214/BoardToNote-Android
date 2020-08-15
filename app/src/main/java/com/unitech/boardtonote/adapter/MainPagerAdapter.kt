package com.unitech.boardtonote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.unitech.boardtonote.fragment.ListCloudFragment
import com.unitech.boardtonote.fragment.ListLocalFragment

class MainPagerAdapter(fm: FragmentActivity)
    : FragmentStateAdapter(fm) {
    private var item: Int = 0

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListLocalFragment()
            1 -> ListCloudFragment()
            else -> throw IllegalArgumentException("MainPagerAdapter $position")
        }
    }
}