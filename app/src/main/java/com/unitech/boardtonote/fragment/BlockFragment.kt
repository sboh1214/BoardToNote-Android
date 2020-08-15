package com.unitech.boardtonote.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.data.BtnInterface
import com.unitech.boardtonote.databinding.FragmentBlockBinding

class BlockFragment(private val blockClass: BtnInterface.BlockClass) : Fragment()
{
    private lateinit var eA: EditActivity
    private lateinit var b: FragmentBlockBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        eA = context as EditActivity
        b = FragmentBlockBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.EditBlock.setText(blockClass.text)
        b.ButtonPlus.setOnClickListener {
            blockClass.fontSize += 2
            onTextSizeChanged()
        }
        b.ButtonMinus.setOnClickListener {
            blockClass.fontSize -= 2
            onTextSizeChanged()
        }
        b.ButtonMore.setOnClickListener {
            BottomBlockFragment(blockClass).show(requireActivity().supportFragmentManager, "bottom_block")
        }
        onTextSizeChanged()
    }

    private fun onTextSizeChanged()
    {
        b.EditSize.text = blockClass.fontSize.toString()
        b.EditBlock.setTextSize(TypedValue.COMPLEX_UNIT_SP, blockClass.fontSize)
        (activity as EditActivity).btnClass.saveContent()
    }

    override fun onPause()
    {
        val imm = eA.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(b.EditBlock.windowToken, 0)
        blockClass.fontSize = b.EditSize.text.toString().toFloat()
        blockClass.text = b.EditBlock.text.toString()
        eA.btnClass.saveContent()
        super.onPause()
    }
}