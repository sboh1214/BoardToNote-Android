package com.unitech.boardtonote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.unitech.boardtonote.fragment.ListLocalFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
{

    override fun getCount(): Int = 100

    override fun getItem(i: Int): Fragment
    {
        return ListLocalFragment()
    }

    override fun getPageTitle(position: Int): CharSequence
    {
        return "OBJECT ${(position + 1)}"
    }
}