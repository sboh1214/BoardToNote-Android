package com.unitech.boardtonote.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import com.unitech.boardtonote.R
import kotlinx.android.synthetic.main.activity_main.*

class PasswordDialog : DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val view = activity!!.layoutInflater.inflate(R.layout.dialog_account, Coor_Main)

            val editEnter = view.findViewById<AppCompatEditText>(R.id.Edit_PasswordEnter)
            val editReEnter = view.findViewById<AppCompatEditText>(R.id.Edit_PasswordReEnter)
            val button = view.findViewById<AppCompatButton>(R.id.Button_Enter)

            button.setOnClickListener {
                //TODO
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}