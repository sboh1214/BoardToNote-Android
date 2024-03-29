package com.unitech.boardtonote.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.MainActivity
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.SnackBarInterface
import java.io.File

class AccountDialog : DialogFragment()
{
    private lateinit var snackBarInterface: SnackBarInterface
    private lateinit var accountInterface: AccountHelper.AccountInterface

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        snackBarInterface = context as SnackBarInterface
        accountInterface = context as AccountHelper.AccountInterface
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_account, null)

            val imageAccount = view.findViewById<AppCompatImageButton>(R.id.Image_Account)
            val editUserName = view.findViewById<AppCompatEditText>(R.id.Edit_UserName)
            val editEmail = view.findViewById<AppCompatEditText>(R.id.Edit_Email)
            val editUid = view.findViewById<AppCompatTextView>(R.id.Edit_Uid)
            if (AccountHelper.user != null)
            {
                Glide.with(view).load(AccountHelper.photoUrl).placeholder(R.drawable.ic_account_gray).into(imageAccount)
                editUserName.setText(AccountHelper.userName)
                editEmail.setText(AccountHelper.email)
                editUid.text = AccountHelper.uid
            }

            val buttonPassword = view.findViewById<AppCompatButton>(R.id.Button_Password)
            val buttonSignOut = view.findViewById<AppCompatButton>(R.id.Button_SignOut)
            val buttonAccountDelete = view.findViewById<AppCompatButton>(R.id.Button_AccountDelete)
            val buttonDismiss = view.findViewById<AppCompatButton>(R.id.Button_Dismiss)
            buttonPassword.setOnClickListener {
                dismiss()
            }
            buttonSignOut.setOnClickListener {
                val a = activity as Context
                val adapter = (activity as MainActivity).cloudAdapter
                AuthUI.getInstance()
                        .signOut(requireContext())
                        .addOnCompleteListener {
                            File("${a.filesDir.path}/cloud").delete()
                            accountInterface.onSignOut(a, adapter)
                            snackBarInterface.snackBar("User account signed out.")
                        }
                dismiss()
            }
            buttonAccountDelete.setOnClickListener {
                val a = activity as Context
                val adapter = (activity as MainActivity).cloudAdapter
                AuthUI.getInstance()
                        .delete(requireContext())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                accountInterface.onSignOut(a, adapter)
                                snackBarInterface.snackBar("User account deleted.")
                            }
                            else
                            {
                                snackBarInterface.snackBar("User account deleted.")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w(tag, "Fail to delete user account")
                            Log.d(tag, e.toString())
                            snackBarInterface.snackBar("Fail to delete user account")
                        }
                dismiss()
            }
            buttonDismiss.setOnClickListener {
                dismiss()
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

    override fun onResume()
    {
        // Sets the height and the width of the DialogFragment
        val width = requireActivity().resources.getDimension(R.dimen.account_width).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)
        super.onResume()
    }
}