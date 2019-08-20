package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        fun bind(btnClass: BTNCloudClass, itemClick: (BTNCloudClass) -> Unit, itemMoreClick: (BTNCloudClass, View) -> Boolean): Boolean
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(btnClass, itemView) }
            Glide.with(itemView).load(btnClass.oriPic).into(itemView.Image_Preview)
            showState(itemView, btnClass.state)
            btnClass.onState = { state -> showState(itemView, state) }
            return true
        }
    }

    private fun showState(itemView: View, state: BTNCloudClass.State): Boolean
    {
        return when (state)
        {
            BTNCloudClass.State.UPLOAD   ->
            {
                itemView.Image_Location.setImageResource(R.drawable.ic_cloud_upload_dark)
                true
            }
            BTNCloudClass.State.DOWNLOAD ->
            {
                itemView.Image_Location.setImageResource(R.drawable.ic_cloud_download_dark)
                true
            }
            BTNCloudClass.State.SYNC     ->
            {
                itemView.Image_Location.setImageResource(R.drawable.ic_cloud_dark)
                true
            }
            else                         ->
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
        holder.bind(listCloudClass.cloudList[position], itemClick, itemMoreClick)
    }

    override fun getItemCount() = listCloudClass.cloudList.size

    override fun getItemId(position: Int): Long = position.toLong()
}