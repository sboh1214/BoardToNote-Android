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
                       private val itemMoreClick: (BTNLocalClass) -> Boolean) :
        RecyclerView.Adapter<ListLocalAdapter.LocalHolder>()
{
    init
    {
        setHasStableIds(true)
    }

    inner class LocalHolder(item: View) : RecyclerView.ViewHolder(item), View.OnClickListener
    {
        fun bind(btnClass: BTNLocalClass)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener(this)
            itemView.Button_Main_More.setOnClickListener { itemMoreClick(btnClass) }
            Glide.with(itemView).load(btnClass.oriPic).centerInside().into(itemView.Image_Preview)
        }

        override fun onClick(view: View?)
        {
            itemClick(listLocalClass.localList[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return LocalHolder(item)
    }

    override fun onBindViewHolder(holder: LocalHolder, position: Int)
    {
        holder.bind(listLocalClass.localList[position])
    }

    override fun getItemCount() = listLocalClass.localList.size

    override fun getItemId(position: Int): Long = position.toLong()

}