package com.unitech.boardtonote.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.data.BtnInterface
import kotlinx.android.synthetic.main.fragment_block.*

class BlockFragment(private val blockClass: BtnInterface.BlockClass) : Fragment()
{
    private lateinit var eA: EditActivity

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        eA = context as EditActivity
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
        Edit_Size.text = blockClass.fontSize.toString()
        Edit_Block.setTextSize(TypedValue.COMPLEX_UNIT_SP, blockClass.fontSize)
        (activity as EditActivity).btnClass.saveContent()
    }

    override fun onPause()
    {
        val imm = eA.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(Edit_Block.windowToken, 0)
        blockClass.fontSize = Edit_Size.text.toString().toFloat()
        blockClass.text = Edit_Block.text.toString()
        eA.btnClass.saveContent()
        super.onPause()
    }
}