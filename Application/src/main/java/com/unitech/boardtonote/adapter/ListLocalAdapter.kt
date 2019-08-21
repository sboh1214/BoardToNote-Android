package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unitech.boardtonote.R
import com.unitech.boardtonote.data.BTNLocalClass
import com.unitech.boardtonote.data.ListLocalClass
import kotlinx.android.synthetic.main.item_main.view.*

class ListLocalAdapter(val listLocalClass: ListLocalClass,
                       private val itemClick: (BTNLocalClass) -> Unit,
                       private val itemMoreClick: (BTNLocalClass, View) -> Boolean) :
        RecyclerView.Adapter<ListLocalAdapter.LocalHolder>()
{
    init
    {
        setHasStableIds(true)
    }

    inner class LocalHolder(item: View) : RecyclerView.ViewHolder(item)
    {
        fun bind(btnClass: BTNLocalClass, itemClick: (BTNLocalClass) -> Unit, itemMoreClick: (BTNLocalClass, View) -> Boolean)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(btnClass, itemView) }
            Glide.with(itemView).load(btnClass.oriPic).centerInside().into(itemView.Image_Preview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return LocalHolder(item)
    }

    override fun onBindViewHolder(holder: LocalHolder, position: Int)
    {
        holder.bind(listLocalClass.localList[position], itemClick, itemMoreClick)
    }

    override fun getItemCount() = listLocalClass.localList.size

    override fun getItemId(position: Int): Long = position.toLong()
}