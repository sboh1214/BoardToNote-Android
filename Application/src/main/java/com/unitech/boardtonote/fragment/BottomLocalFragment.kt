package com.unitech.boardtonote.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.MainActivity
import com.unitech.boardtonote.data.BtnLocal
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.bottom_local.view.*
import kotlinx.android.synthetic.main.dialog_rename.view.*

class BottomLocalFragment(private val btn: BtnLocal) : BottomSheetDialogFragment()
{
    private lateinit var mA: MainActivity
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        snackBarInterface = context as SnackBarInterface
        mA = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.bottom_local, container, false)
        view.Text_Title.text = btn.dirName
        view.Button_Rename.setOnClickListener {
            rename(btn)
            dismiss()
        }
        view.Button_Delete.setOnClickListener {
            delete(btn)
            dismiss()
        }
        view.Button_Upload.setOnClickListener {
            if (AccountHelper.user == null)
            {
                snackBarInterface.snackBar("You should log in first.")
                dismiss()
                return@setOnClickListener
            }
            mA.cloudAdapter.btnCloudList.moveFromLocal(mA.localAdapter.btnLocalList, btn)
            mA.localAdapter.notifyDataSetChanged()
            mA.cloudAdapter.notifyDataSetChanged()
            dismiss()
        }
        view.Button_Share.setOnClickListener {
            btn.share(Constant.sharePdf)
            dismiss()
        }
        return view
    }

    private fun rename(btnLocal: BtnLocal)
    {
        val srcName = btn.dirName
        var dstName: String?

        AlertDialog.Builder(activity as Context).apply {
            setTitle("Rename Note")
            val view = layoutInflater.inflate(R.layout.dialog_rename, null)
            setPositiveButton("Rename") { _, _ ->
                dstName = view.Edit_Rename.text.toString()
                mA.localAdapter.btnLocalList.rename(btnLocal, view.Edit_Rename.text.toString())
                mA.localAdapter.notifyDataSetChanged()
                snackBarInterface.snackBar("$srcName renamed to $dstName")
            }
            setNegativeButton("Cancel") { _, _ ->
                snackBarInterface.snackBar("User canceled renaming $srcName")
            }
            setView(view)
        }.show()
    }

    fun delete(btnLocal: BtnLocal)
    {
        mA.localAdapter.btnLocalList.delete(btnLocal)
        mA.localAdapter.notifyDataSetChanged()
        snackBarInterface.snackBar("${btn.dirName} deleted")
    }
}