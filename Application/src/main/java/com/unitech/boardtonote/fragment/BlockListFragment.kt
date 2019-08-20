package com.unitech.boardtonote.fragment

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.adapter.BlockAdapter
import com.unitech.boardtonote.data.BTNInterface
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.fragment_edit.*

class BlockListFragment : Fragment()
{
    private lateinit var editActivity: EditActivity
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        editActivity = activity as EditActivity
        snackBarInterface = activity as SnackBarInterface
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        editActivity.btnClass.asyncGetContent({ content -> onSuccess(content) }, { onFailure() })
        loadImage()
    }

    private fun onSuccess(content: BTNInterface.ContentClass): Boolean
    {
        Log.i(tag, "Recycler_Edit Init")
        editActivity.blockManager = LinearLayoutManager(editActivity)
        editActivity.blockAdapter = BlockAdapter(content.blockList,
                { btnClass -> itemClick(btnClass) },
                { btnClass -> itemLongClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        Recycler_Edit.apply {
            setHasFixedSize(false)
            layoutManager = editActivity.blockManager
            adapter = editActivity.blockAdapter
        }
        return true
    }

    private fun onFailure(): Boolean
    {
        Log.w(tag, "Recycler_Edit Error")
        return true
    }

    private fun itemClick(blockClass: BTNInterface.BlockClass)
    {
        editActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.Frame_Edit, BlockFragment(blockClass))
                .commit()
        return
    }

    private fun loadImage()
    {
        val point = Point()
        editActivity.windowManager.defaultDisplay.getSize(point)
        Image_OriPic.setImageBitmap(editActivity.btnClass.decodeOriPic(point.x, null))
        val ratio = editActivity.btnClass.getOriPicRatio()
        if (ratio != null)
        {
            val imageHeight = (point.x.toFloat() * ratio).toInt()
            Image_OriPic.layoutParams.height = imageHeight
            Image_OriPic.requestLayout()
        }

    }

    private fun itemLongClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        return true
    }

    private fun itemMoreClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        BottomBlockFragment(btnClass).show(editActivity.supportFragmentManager, "bottom_block")
        return true
    }
}