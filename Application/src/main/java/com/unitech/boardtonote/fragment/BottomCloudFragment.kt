package com.unitech.boardtonote.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.unitech.boardtonote.R
import com.unitech.boardtonote.adapter.ListCloudAdapter
import com.unitech.boardtonote.data.BTNCloudClass
import com.unitech.boardtonote.data.BTNInterface
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.bottom_local.view.*
import kotlinx.android.synthetic.main.dialog_rename.*

class BottomCloudFragment(private val cloudAdapter: ListCloudAdapter, private val btnClass: BTNCloudClass) : BottomSheetDialogFragment()
{
    private lateinit var snackBarInterface: SnackBarInterface

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        snackBarInterface = context as SnackBarInterface
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.bottom_cloud, container, false)
        view.Text_Title.text = btnClass.dirName
        view.Button_Rename.setOnClickListener {
            rename(btnClass)
            dismiss()
        }
        view.Button_Delete.setOnClickListener {
            delete(btnClass)
            dismiss()
        }
        return view
    }

    private fun rename(btnClass: BTNInterface)
    {
        val srcName = btnClass.dirName
        var dstName: String?

        AlertDialog.Builder(activity as Context).apply {
            setTitle("Rename Note")
            val view = layoutInflater.inflate(R.layout.dialog_rename, container, false)
            setPositiveButton("Rename") { _, _ ->
                dstName = Edit_Rename.toString()
                //cloudAdapter.listCloudClass.rename(Edit_Rename.text.toString())
                cloudAdapter.notifyDataSetChanged()
                snackBarInterface.snackBar("$srcName renamed to $dstName")
            }
            setNegativeButton("Cancel") { _, _ ->
                snackBarInterface.snackBar("User canceled renaming $srcName")
            }
            setView(view)
        }.show()
    }

    fun delete(btnClass: BTNInterface)
    {
        //cloudAdapter.listCloudClass.delete()
        cloudAdapter.notifyDataSetChanged()
        snackBarInterface.snackBar("${btnClass.dirName} deleted")
    }
}