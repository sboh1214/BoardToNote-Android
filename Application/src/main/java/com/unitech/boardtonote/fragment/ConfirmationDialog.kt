package com.unitech.boardtonote.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.unitech.boardtonote.R

class ConfirmationDialog : DialogFragment()
{

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity as Context)
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val requestCameraPermission = 1
                        parentFragment?.requestPermissions(arrayOf(Manifest.permission.CAMERA),
                                requestCameraPermission)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        parentFragment?.activity?.finish()
                    }
                    .create()
}