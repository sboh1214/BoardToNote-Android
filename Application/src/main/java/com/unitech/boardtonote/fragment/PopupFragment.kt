package com.unitech.boardtonote.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.unitech.boardtonote.R
import com.unitech.boardtonote.data.LocalBTNClass
import kotlinx.android.synthetic.main.fragment_popup.view.*

class PopupFragment(private var btnClass: LocalBTNClass) : BottomSheetDialogFragment()
{
    private lateinit var popupListener: PopupListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)
        view.Text_Title.text = btnClass.dirName
        view.Button_Rename.setOnClickListener {
            popupListener.rename(btnClass)
            dismiss()
        }
        view.Button_Delete.setOnClickListener {
            popupListener.delete(btnClass)
            dismiss()
        }
        return view
    }

    override fun onAttach(context: Context)
    {
        if (context is PopupListener)
        {
            popupListener = context
        }
        super.onAttach(context)
    }

    interface PopupListener
    {
        fun rename(btnClass: LocalBTNClass)
        fun delete(btnClass: LocalBTNClass)
    }
}