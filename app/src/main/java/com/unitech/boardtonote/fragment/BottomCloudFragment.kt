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
import com.unitech.boardtonote.data.BtnCloud
import com.unitech.boardtonote.databinding.BottomCloudBinding
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.dialog_rename.view.*

class BottomCloudFragment(private val btn: BtnCloud) : BottomSheetDialogFragment()
{
    private lateinit var mainActivity: MainActivity
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        snackBarInterface = context as SnackBarInterface
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val b = BottomCloudBinding.inflate(inflater, container, false)
        b.TextTitle.text = btn.dirName
        b.ButtonRename.setOnClickListener {
            rename(btn)
            dismiss()
        }
        b.ButtonDelete.setOnClickListener {
            delete(btn)
            dismiss()
        }
        b.ButtonShare.setOnClickListener {
            btn.share(Constant.sharePdf)
            dismiss()
        }
        return b.root
    }

    private fun rename(btnCloud: BtnCloud)
    {
        val srcName = btn.dirName
        var dstName: String?

        AlertDialog.Builder(activity as Context).apply {
            setTitle("Rename Note")
            val view = layoutInflater.inflate(R.layout.dialog_rename, null)
            setPositiveButton("Rename") { _, _ ->
                dstName = view.Edit_Rename.text.toString()
                mainActivity.cloudAdapter.btnCloudList.rename(btnCloud, view.Edit_Rename.text.toString())
                mainActivity.cloudAdapter.notifyDataSetChanged()
                snackBarInterface.snackBar("$srcName renamed to $dstName")
            }
            setNegativeButton("Cancel") { _, _ ->
                snackBarInterface.snackBar("User canceled renaming $srcName")
            }
            setView(view)
        }.show()
    }

    fun delete(btn: BtnCloud)
    {
        mainActivity.cloudAdapter.btnCloudList.delete(btn)
        mainActivity.cloudAdapter.notifyDataSetChanged()
        snackBarInterface.snackBar("${btn.dirName} deleted")
    }
}