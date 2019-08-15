package com.unitech.boardtonote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.unitech.boardtonote.fragment.ListCloudFragment
import com.unitech.boardtonote.fragment.ListLocalFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
{

    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment
    {
        return when (i)
        {
            0    -> ListLocalFragment()
            1    -> ListCloudFragment()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getPageTitle(position: Int): CharSequence
    {
        return when (position)
        {
            0    -> "Local"
            1    -> "Cloud"
            else -> "Error"
        }
    }
}