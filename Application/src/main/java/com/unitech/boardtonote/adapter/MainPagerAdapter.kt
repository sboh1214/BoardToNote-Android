package com.unitech.boardtonote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.unitech.boardtonote.data.BTNInterface
import com.unitech.boardtonote.fragment.ListCloudFragment
import com.unitech.boardtonote.fragment.ListLocalFragment

class MainPagerAdapter(fm: FragmentManager)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
{
    private var item: Int = 0

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment
    {
        return when (position)
        {
            BTNInterface.Location.LOCAL.value -> ListLocalFragment()
            BTNInterface.Location.CLOUD.value -> ListCloudFragment()
            else                              -> throw IllegalArgumentException("MainPagerAdapter $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence
    {
        return when (position)
        {
            BTNInterface.Location.LOCAL.value -> "Local"
            BTNInterface.Location.CLOUD.value -> "Cloud"
            else                              -> throw IllegalArgumentException("MainPagerAdapter $position")
        }
    }
}