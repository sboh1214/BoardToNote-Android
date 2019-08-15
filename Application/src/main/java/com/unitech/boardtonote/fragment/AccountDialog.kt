package com.unitech.boardtonote.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.unitech.boardtonote.R
import com.unitech.boardtonote.helper.SnackBarInterface

class AccountDialog : DialogFragment()
{
    private lateinit var snackBarInterface: SnackBarInterface
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        snackBarInterface = context as SnackBarInterface
        Log.i(tag, "onCreate")
        Log.v(tag, "displayName : ${user?.displayName}")
        Log.v(tag, "email       : ${user?.email}")
        Log.v(tag, "uid         : ${user?.uid}")
        Log.v(tag, "photoUrl    : ${user?.photoUrl}")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val view = activity!!.layoutInflater.inflate(R.layout.dialog_account, null)

            val imageAccount = view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.Image_Account)
            val editUserName = view.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.Edit_UserName)
            val editEmail = view.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.Edit_Email)
            val editUid = view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.Edit_Uid)
            if (user != null)
            {
                Glide.with(view).load(user.photoUrl).into(imageAccount)
                editUserName.setText(user.displayName)
                editEmail.setText(user.email)
                editUid.text = user.uid
            }

            val buttonPassword = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.Button_Password)
            val buttonSignOut = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.Button_SignOut)
            val buttonAccountDelete = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.Button_AccountDelete)
            buttonPassword.setOnClickListener {
                dismiss()
            }
            buttonSignOut.setOnClickListener {
                AuthUI.getInstance()
                        .signOut(context!!)
                        .addOnCompleteListener {
                            Log.i(tag, "User account signed out.")
                            snackBarInterface.snackBar("User account signed out.")
                        }
                dismiss()
            }
            buttonAccountDelete.setOnClickListener {
                user?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        Log.i(tag, "User account deleted.")
                        snackBarInterface.snackBar("User account deleted.")
                        dismiss()
                    }
                }
                dismiss()
            }
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}