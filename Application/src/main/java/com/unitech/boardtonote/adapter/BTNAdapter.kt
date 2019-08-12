package com.unitech.boardtonote.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.unitech.boardtonote.R
import com.unitech.boardtonote.data.LocalBTNClass
import kotlinx.android.synthetic.main.item_main.view.*

class BTNAdapter(private val btnList: ArrayList<LocalBTNClass>,
                 private val itemClick: (LocalBTNClass) -> Unit,
                 private val itemMoreClick: (LocalBTNClass, View) -> Boolean) :
        RecyclerView.Adapter<BTNAdapter.BTNHolder>()
{
    lateinit var tracker: SelectionTracker<Long>

    init
    {
        setHasStableIds(true)
    }

    inner class BTNHolder(item: View) : RecyclerView.ViewHolder(item)
    {
        fun bind(btnClass: LocalBTNClass, itemClick: (LocalBTNClass) -> Unit, itemMoreClick: (LocalBTNClass, View) -> Boolean, isActivated: Boolean = false)
        {
            itemView.Title_Text.text = btnClass.dirName
            itemView.setOnClickListener { itemClick(btnClass) }
            itemView.Button_More.setOnClickListener { itemMoreClick(btnClass, itemView) }
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
                object : ItemDetailsLookup.ItemDetails<Long>()
                {
                    override fun getPosition(): Int = adapterPosition
                    override fun getSelectionKey(): Long? = itemId
                }
    }

    fun rename(btnClass: LocalBTNClass, name: String)
    {
        btnClass.rename(name)
        notifyDataSetChanged()
    }

    fun delete(btnClass: LocalBTNClass)
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
        holder.bind(btnList[position], itemClick, itemMoreClick, tracker.isSelected(position.toLong()))
    }

    override fun getItemCount() = btnList.size

    override fun getItemId(position: Int): Long = position.toLong()
}

class MyLookup(private val rv: RecyclerView) : ItemDetailsLookup<Long>()
{
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>?
    {

        val view = rv.findChildViewUnder(event.x, event.y)
        if (view != null)
        {
            return (rv.getChildViewHolder(view) as BTNAdapter.BTNHolder)
                    .getItemDetails()
        }
        return null
    }
}