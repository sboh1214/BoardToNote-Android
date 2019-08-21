package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.data.BTNInterface
import kotlinx.android.synthetic.main.item_edit.view.*


class BlockAdapter(val btnInterface: BTNInterface,
                   private val itemClick: (BTNInterface.BlockClass) -> Unit,
                   private val itemLongClick: (BTNInterface.BlockClass) -> Boolean,
                   private val itemMoreClick: (BTNInterface.BlockClass, View) -> Boolean) :
        RecyclerView.Adapter<BlockAdapter.BlockHolder>()
{
    inner class BlockHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun bind(blockClass: BTNInterface.BlockClass,
                 itemClick: (BTNInterface.BlockClass) -> Unit,
                 itemLongClick: (BTNInterface.BlockClass) -> Boolean,
                 itemMoreClick: (BTNInterface.BlockClass, View) -> Boolean)
        {
            itemView.Text_Content.text = blockClass.text
            itemView.setOnClickListener { itemClick(blockClass) }
            itemView.setOnLongClickListener { itemLongClick(blockClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(blockClass, itemView) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(com.unitech.boardtonote.R.layout.item_edit, parent, false)
        return BlockHolder(item)
    }

    override fun onBindViewHolder(holder: BlockHolder, position: Int)
    {
        holder.bind(btnInterface.content.blockList[position], itemClick, itemLongClick, itemMoreClick)
    }

    override fun getItemCount() = btnInterface.content.blockList.size
}
