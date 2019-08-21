package com.unitech.boardtonote.fragment

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
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
    private lateinit var eA: EditActivity
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        eA = activity as EditActivity
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
        eA.btnClass.asyncGetContent({ content -> onSuccess(content) }, { onFailure() })
        loadImage()
    }

    private fun onSuccess(content: BTNInterface.ContentClass): Boolean
    {
        Log.i(tag, "Recycler_Edit Init")
        eA.blockAdapter = BlockAdapter(content.blockList,
                { btnClass -> itemClick(btnClass) },
                { btnClass -> itemLongClick(btnClass) },
                { btnClass, _ -> itemMoreClick(btnClass) })

        Recycler_Edit.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(eA)
            adapter = eA.blockAdapter
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
        eA.supportFragmentManager
                .beginTransaction()
                .replace(R.id.Frame_Edit, BlockFragment(blockClass))
                .commit()
        return
    }

    private fun loadImage()
    {
        val point = Point()
        eA.windowManager.defaultDisplay.getSize(point)
        Image_OriPic.setImageBitmap(eA.btnClass.decodeOriPic(point.x, null))
        val ratio = eA.btnClass.getOriPicRatio()
        if (ratio != null)
        {
            val imageHeight = (point.x.toFloat() * ratio).toInt()
            Image_OriPic.layoutParams.height = imageHeight
            Image_OriPic.requestLayout()
            val displayMetrics = DisplayMetrics()
            eA.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val param = Recycler_Edit.layoutParams
            val type = TypedValue()
            eA.theme.resolveAttribute(R.attr.actionBarSize, type, true)
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(type.data, eA.resources.displayMetrics)
            param.height = height - imageHeight - actionBarHeight
            Recycler_Edit.layoutParams = param
        }

    }

    private fun itemLongClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        return true
    }

    private fun itemMoreClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        BottomBlockFragment(btnClass).show(eA.supportFragmentManager, "bottom_block")
        return true
    }
}