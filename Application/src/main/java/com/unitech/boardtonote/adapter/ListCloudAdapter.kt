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
import kotlinx.android.synthetic.main.item_main.view.*

class ListCloudAdapter(val btnCloudList: BtnCloudList,
                       private val itemClick: (BtnCloud) -> Unit,
                       private val itemMoreClick: (BtnCloud) -> Boolean) :
        RecyclerView.Adapter<ListCloudAdapter.ListCloudHolder>()
{
    init
    {
        setHasStableIds(true)
    }

    inner class ListCloudHolder(item: View) : RecyclerView.ViewHolder(item), View.OnClickListener
    {
        fun bind(btn: BtnCloud): Boolean
        {
            itemView.Title_Text.text = btn.dirName
            itemView.setOnClickListener(this)
            itemView.Button_Main_More.setOnClickListener { itemMoreClick(btn) }
            Glide.with(itemView).load(btn.oriPic).centerInside().into(itemView.Image_Preview)
            showState(itemView, btn, btn.state)
            btn.onLocationAndState = { _, state -> showState(itemView, btn, state) }
            return true
        }

        override fun onClick(view: View?)
        {
            itemClick(btnCloudList.cloudList[adapterPosition])
        }
    }

    private fun showState(itemView: View, btn: BtnCloud, state: Int?): Boolean
    {
        return when (state)
        {
            Constant.stateUpload   ->
            {
                Glide.with(itemView).asGif().load(R.raw.ic_upload_light).into(itemView.Image_Location)
                true
            }
            Constant.stateDownload ->
            {
                Glide.with(itemView).asGif().load(R.raw.ic_download_light).into(itemView.Image_Location)
                true
            }
            Constant.stateSync     ->
            {
                Glide.with(itemView).asGif().load(R.raw.ic_sync_light).into(itemView.Image_Location)
                Glide.with(itemView).load(btn.oriPic).centerInside().into(itemView.Image_Preview)
                true
            }
            else                   ->
            {
                itemView.Image_Location.setImageResource(R.drawable.ic_error_dark)
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCloudHolder
    {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ListCloudHolder(item)
    }

    override fun onBindViewHolder(holder: ListCloudHolder, position: Int)
    {
        holder.bind(btnCloudList.cloudList[position])
    }

    override fun getItemCount() = btnCloudList.cloudList.size

    override fun getItemId(position: Int): Long = position.toLong()
}