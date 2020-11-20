package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.data.BtnLocalList
import com.unitech.boardtonote.databinding.ItemMainBinding

class ListLocalAdapter(val btnLocalList: BtnLocalList,
                       private val itemClick: (BtnLocal) -> Unit,
                       private val itemMoreClick: (BtnLocal) -> Boolean) :
        RecyclerView.Adapter<ListLocalAdapter.LocalHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class LocalHolder(private val binding: ItemMainBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(btn: BtnLocal) {
            binding.TitleText.text = btn.dirName
            binding.root.setOnClickListener(this)
            binding.ButtonMainMore.setOnClickListener { itemMoreClick(btn) }
            Glide.with(binding.root).load(btn.oriPic).centerInside().into(binding.ImagePreview)
        }

        override fun onClick(view: View?)
        {
            itemClick(btnLocalList.localList[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocalHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalHolder, position: Int)
    {
        holder.bind(btnLocalList.localList[position])
    }

    override fun getItemCount() = btnLocalList.localList.size

    override fun getItemId(position: Int): Long = position.toLong()

}