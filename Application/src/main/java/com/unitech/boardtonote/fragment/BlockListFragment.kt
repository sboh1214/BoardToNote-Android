package com.unitech.boardtonote.fragment

import android.content.Context
import android.content.res.Configuration
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.unitech.boardtonote.Constant
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
        eA.btnClass.asyncGetContent({ onSuccess() }, { onFailure() })
        if (eA.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            render(Constant.orientationLandscape)
        }
        else
        {
            render(Constant.orientationPortrait)
        }
    }

    private fun onSuccess(): Boolean
    {
        Log.i(tag, "Recycler_Edit Init")
        eA.blockAdapter = BlockAdapter(eA.btnClass,
                { btnClass, itemView -> itemClick(btnClass, itemView) },
                { btnClass -> itemMoreClick(btnClass) })

        Recycler_Edit.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(eA)
            adapter = eA.blockAdapter
        }
        eA.blockAdapter.btnInterface.onLocationAndState = { a, b -> eA.showLocationAndState(a, b) }
        return true
    }

    private fun onFailure(): Boolean
    {
        Log.w(tag, "Recycler_Edit Error")
        return true
    }

    private fun itemClick(blockClass: BTNInterface.BlockClass, itemView: View)
    {
        eA.supportFragmentManager
                .beginTransaction()
                .replace(R.id.Frame_Edit, BlockFragment(blockClass))
                .addToBackStack(null)
                .commit()
    }

    private fun itemMoreClick(btnClass: BTNInterface.BlockClass): Boolean
    {
        val dialog = BottomBlockFragment(btnClass)

        val contentView = View.inflate(context, R.layout.bottom_block, null)

        val displayMetrics = activity!!.resources.displayMetrics

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val maxHeight = (height * 0.88).toInt()

        val mBehavior = BottomSheetBehavior.from(contentView.parent as View)
        mBehavior.peekHeight = maxHeight
        dialog.show(eA.supportFragmentManager, null)
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            render(Constant.orientationLandscape)
        }
        else
        {
            render(Constant.orientationPortrait)
        }
    }

    private fun render(orientation: Int)
    {
        Image_OriPic.setImageBitmap(eA.btnClass.oriPic)
        if (orientation == Constant.orientationPortrait)
        {
            val ratio = eA.btnClass.getOriPicRatio()
            if (ratio != null)
            {
                val point = Point()
                eA.windowManager.defaultDisplay.getSize(point)
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
    }
}