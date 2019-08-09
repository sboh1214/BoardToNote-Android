package com.unitech.boardtonote.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.BTNClass
import com.unitech.boardtonote.R
import kotlinx.android.synthetic.main.item_edit.view.*

class BlockAdapter(private val blockList: ArrayList<BTNClass.BlockClass>,
                   private val itemClick: (BTNClass.BlockClass) -> Unit,
                   private val itemLongClick: (BTNClass.BlockClass) -> Boolean,
                   private val itemMoreClick: (BTNClass.BlockClass, View) -> Boolean) :
        RecyclerView.Adapter<BlockAdapter.BlockHolder>()
{
    inner class BlockHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener
    {
        fun bind(blockClass: BTNClass.BlockClass,
                 itemClick: (BTNClass.BlockClass) -> Unit,
                 itemLongClick: (BTNClass.BlockClass) -> Boolean,
                 itemMoreClick: (BTNClass.BlockClass, View) -> Boolean)
        {
            itemView.Text_Content.text = blockClass.text
            itemView.setOnClickListener { itemClick(blockClass) }
            itemView.setOnLongClickListener { itemLongClick(blockClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(blockClass, itemView) }
        }

        override fun onCreateContextMenu(menu: ContextMenu?, view: View?, info: ContextMenu.ContextMenuInfo?)
        {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_edit, parent, false)
        return BlockHolder(item)
    }

    override fun onBindViewHolder(holder: BlockHolder, position: Int)
    {
        holder.bind(blockList[position], itemClick, itemLongClick, itemMoreClick)
    }


    override fun getItemCount() = blockList.size
}
