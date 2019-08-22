package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.data.BTNInterface
import kotlinx.android.synthetic.main.item_edit.view.*


class BlockAdapter(val btnInterface: BTNInterface,
                   private val itemClick: (BTNInterface.BlockClass, View) -> Unit,
                   private val itemMoreClick: (BTNInterface.BlockClass) -> Boolean) :
        RecyclerView.Adapter<BlockAdapter.BlockHolder>()
{
    inner class BlockHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        fun bind(blockClass: BTNInterface.BlockClass)
        {
            itemView.Text_Content.text = blockClass.text
            itemView.Text_Content.textSize = blockClass.fontSize
            itemView.setOnClickListener(this)
            itemView.Button_Edit_More.setOnClickListener { itemMoreClick(blockClass) }
        }

        override fun onClick(view: View?)
        {
            itemClick(btnInterface.content.blockList[adapterPosition], view!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(com.unitech.boardtonote.R.layout.item_edit, parent, false)
        return BlockHolder(item)
    }

    override fun onBindViewHolder(holder: BlockHolder, position: Int)
    {
        holder.bind(btnInterface.content.blockList[position])
    }

    override fun getItemCount() = btnInterface.content.blockList.size
}
