package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.data.BtnInterface
import com.unitech.boardtonote.databinding.ItemEditBinding


class BlockAdapter(val btnInterface: BtnInterface,
                   private val itemClick: (BtnInterface.BlockClass, View) -> Unit,
                   private val itemMoreClick: (BtnInterface.BlockClass) -> Boolean) :
        RecyclerView.Adapter<BlockAdapter.BlockHolder>() {
    inner class BlockHolder(private val binding: ItemEditBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(blockClass: BtnInterface.BlockClass) {
            binding.TextContent.text = blockClass.text
            binding.TextContent.textSize = blockClass.fontSize
            binding.root.setOnClickListener(this)
            binding.ButtonEditMore.setOnClickListener { itemMoreClick(blockClass) }
        }

        override fun onClick(view: View?)
        {
            itemClick(btnInterface.content!!.blockList[adapterPosition], view!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockHolder {
        val binding = ItemEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockHolder, position: Int)
    {
        holder.bind(btnInterface.content!!.blockList[position])
    }

    override fun getItemCount() = btnInterface.content!!.blockList.size
}
