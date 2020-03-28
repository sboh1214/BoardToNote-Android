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
import com.unitech.boardtonote.databinding.BottomLocalBinding
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.dialog_rename.view.*

class BottomLocalFragment(private val btn: BtnLocal) : BottomSheetDialogFragment() {
    private lateinit var mA: MainActivity
    private lateinit var b: BottomLocalBinding
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        snackBarInterface = context as SnackBarInterface
        mA = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        b = BottomLocalBinding.inflate(inflater, container, false)
        b.TextTitle.text = btn.dirName
        b.ButtonRename.setOnClickListener {
            rename(btn)
            dismiss()
        }
        b.ButtonDelete.setOnClickListener {
            delete(btn)
            dismiss()
        }
        b.ButtonUpload.setOnClickListener {
            if (AccountHelper.user == null) {
                snackBarInterface.snackBar("You should log in first.")
                dismiss()
                return@setOnClickListener
            }
            mA.cloudAdapter.btnCloudList.moveFromLocal(mA.localAdapter.btnLocalList, btn)
            mA.localAdapter.notifyDataSetChanged()
            mA.cloudAdapter.notifyDataSetChanged()
            dismiss()
        }
        b.ButtonShare.setOnClickListener {
            btn.share(Constant.sharePdf)
            dismiss()
        }
        return b.root
    }

    private fun rename(btnLocal: BtnLocal) {
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

    fun delete(btnLocal: BtnLocal) {
        mA.localAdapter.btnLocalList.delete(btnLocal)
        mA.localAdapter.notifyDataSetChanged()
        snackBarInterface.snackBar("${btn.dirName} deleted")
    }
}