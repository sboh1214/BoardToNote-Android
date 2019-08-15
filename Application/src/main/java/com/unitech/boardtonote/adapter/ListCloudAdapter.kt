package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.R
import com.unitech.boardtonote.data.BTNCloudClass
import com.unitech.boardtonote.data.ListCloudClass
import kotlinx.android.synthetic.main.item_main.view.*

class ListCloudAdapter(val listCloudClass: ListCloudClass,
                       private val itemClick: (BTNCloudClass) -> Unit,
                       private val itemMoreClick: (BTNCloudClass, View) -> Boolean) :
        RecyclerView.Adapter<ListCloudAdapter.ListCloudHolder>()
{
    init
    {
        setHasStableIds(true)
    }

    inner class ListCloudHolder(item: View) : RecyclerView.ViewHolder(item)
    {
        fun bind(btnClass: BTNCloudClass, itemClick: (BTNCloudClass) -> Unit, itemMoreClick: (BTNCloudClass, View) -> Boolean)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(btnClass, itemView) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCloudHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ListCloudHolder(item)
    }

    override fun onBindViewHolder(holder: ListCloudHolder, position: Int)
    {
        holder.bind(listCloudClass.dirList[position], itemClick, itemMoreClick)
    }

    override fun getItemCount() = listCloudClass.dirList.size

    override fun getItemId(position: Int): Long = position.toLong()
}