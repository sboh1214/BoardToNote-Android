package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.BTNClass
import com.unitech.boardtonote.R
import kotlinx.android.synthetic.main.item_main.view.*

class BTNAdapter(private val btnList: ArrayList<BTNClass>,
                 private val itemClick: (BTNClass) -> Unit,
                 private val itemLongClick: (BTNClass) -> Boolean,
                 private val itemMoreClick: (BTNClass, View) -> Boolean) :
        RecyclerView.Adapter<BTNAdapter.BTNHolder>()
{
    inner class BTNHolder(item: View) : RecyclerView.ViewHolder(item)
    {
        fun bind(btnClass: BTNClass, itemClick: (BTNClass) -> Unit, itemLongClick: (BTNClass) -> Boolean, itemMoreClick: (BTNClass, View) -> Boolean)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.setOnLongClickListener { itemLongClick(btnClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(btnClass, itemView) }
        }
    }

    fun rename(btnClass: BTNClass, name: String)
    {
        btnClass.rename(name)
        notifyDataSetChanged()
    }

    fun delete(btnClass: BTNClass)
    {
        btnClass.delete()
        btnList.remove(btnClass)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BTNHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return BTNHolder(item)
    }

    override fun onBindViewHolder(holder: BTNHolder, position: Int)
    {
        holder.bind(btnList[position], itemClick, itemLongClick, itemMoreClick)
    }

    override fun getItemCount() = btnList.size
}