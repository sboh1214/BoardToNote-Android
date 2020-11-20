package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.data.BtnCloud
import com.unitech.boardtonote.data.BtnCloudList
import com.unitech.boardtonote.databinding.ItemMainBinding

class ListCloudAdapter(val btnCloudList: BtnCloudList,
                       private val itemClick: (BtnCloud) -> Unit,
                       private val itemMoreClick: (BtnCloud) -> Boolean) :
        RecyclerView.Adapter<ListCloudAdapter.ListCloudHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class ListCloudHolder(private val binding: ItemMainBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(btn: BtnCloud): Boolean {
            binding.TitleText.text = btn.dirName
            binding.root.setOnClickListener(this)
            binding.ButtonMainMore.setOnClickListener { itemMoreClick(btn) }
            Glide.with(itemView).load(btn.oriPic).centerInside().into(binding.ImagePreview)
            showState(binding, btn, btn.state)
            btn.onLocationAndState = { _, state -> showState(binding, btn, state) }
            return true
        }

        override fun onClick(view: View?)
        {
            itemClick(btnCloudList.cloudList[adapterPosition])
        }
    }

    private fun showState(b: ItemMainBinding, btn: BtnCloud, state: Int?): Boolean {
        return when (state) {
            Constant.stateUpload -> {
                Glide.with(b.root).asGif().load(R.raw.ic_upload_light).into(b.ImageLocation)
                true
            }
            Constant.stateDownload -> {
                Glide.with(b.root).asGif().load(R.raw.ic_download_light).into(b.ImageLocation)
                true
            }
            Constant.stateSync -> {
                Glide.with(b.root).asGif().load(R.raw.ic_sync_light).into(b.ImageLocation)
                Glide.with(b.root).load(btn.oriPic).centerInside().into(b.ImagePreview)
                true
            }
            else                   ->
            {
                b.ImageLocation.setImageResource(R.drawable.ic_error_dark)
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCloudHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListCloudHolder(binding)
    }

    override fun onBindViewHolder(holder: ListCloudHolder, position: Int)
    {
        holder.bind(btnCloudList.cloudList[position])
    }

    override fun getItemCount() = btnCloudList.cloudList.size

    override fun getItemId(position: Int): Long = position.toLong()
}