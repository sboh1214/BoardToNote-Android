package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unitech.boardtonote.R
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.data.BtnLocalList
import kotlinx.android.synthetic.main.item_main.view.*

class ListLocalAdapter(val btnLocalList: BtnLocalList,
                       private val itemClick: (BtnLocal) -> Unit,
                       private val itemMoreClick: (BtnLocal) -> Boolean) :
        RecyclerView.Adapter<ListLocalAdapter.LocalHolder>()
{
    init
    {
        setHasStableIds(true)
    }

    inner class LocalHolder(item: View) : RecyclerView.ViewHolder(item), View.OnClickListener
    {
        fun bind(btn: BtnLocal)
        {
            itemView.Title_Text.text = btn.dirName
            itemView.setOnClickListener(this)
            itemView.Button_Main_More.setOnClickListener { itemMoreClick(btn) }
            Glide.with(itemView).load(btn.oriPic).centerInside().into(itemView.Image_Preview)
        }

        override fun onClick(view: View?)
        {
            itemClick(btnLocalList.localList[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return LocalHolder(item)
    }

    override fun onBindViewHolder(holder: LocalHolder, position: Int)
    {
        holder.bind(btnLocalList.localList[position])
    }

    override fun getItemCount() = btnLocalList.localList.size

    override fun getItemId(position: Int): Long = position.toLong()

}