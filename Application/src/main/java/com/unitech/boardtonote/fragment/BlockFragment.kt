package com.unitech.boardtonote.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.data.BTNInterface
import kotlinx.android.synthetic.main.fragment_block.*

class BlockFragment(private val blockClass: BTNInterface.BlockClass) : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_block, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Edit_Block.setText(blockClass.text)
        Button_Plus.setOnClickListener {
            blockClass.fontSize += 2
            onTextSizeChanged()
        }
        Button_Minus.setOnClickListener {
            blockClass.fontSize -= 2
            onTextSizeChanged()
        }
        Button_More.setOnClickListener {
            BottomBlockFragment(blockClass).show(activity!!.supportFragmentManager, "bottom_block")
        }
        onTextSizeChanged()
    }

    private fun onTextSizeChanged()
    {
        Edit_Size.setText(blockClass.fontSize.toString())
        Edit_Block.setTextSize(TypedValue.COMPLEX_UNIT_SP, blockClass.fontSize)
        (activity as EditActivity).btnClass.saveContent()
    }
}