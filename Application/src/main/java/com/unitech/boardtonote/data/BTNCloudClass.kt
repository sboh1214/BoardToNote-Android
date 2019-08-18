package com.unitech.boardtonote.data

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.unitech.boardtonote.helper.AccountHelper
import java.io.File

class BTNCloudClass(override val context: Context, override var dirName: String?) : BTNInterface
{
    override val tag = "BTNCloudClass"

    private val firebaseUrl = "gs://board-to-note.appspot.com/"

    private var state: BTNInterface.State = BTNInterface.State.LOCAL

    init
    {
        if (!File(parentDirPath).exists())
        {
            File(parentDirPath).mkdir()
        }
        // make local directory if it does not exist
        if (dirName == null)
        {
            makeLocalDir(null)
        }
        else if (!File(dirPath).exists())
        {
            makeLocalDir(dirName)
        }
    }

    override lateinit var content: BTNInterface.ContentClass

    override val parentDirPath: String
        get() = "${context.filesDir.path}/cloud"

    override val dirPath: String
        get() = "$parentDirPath/$dirName.btn"

    fun download(onSuccess: () -> Boolean, onFailure: () -> Boolean)
    {
        state = BTNInterface.State.DOWNLOAD
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("${AccountHelper.uid}/$dirName.btn")
        val file = File(dirPath)
        reference.getFile(file)
                .addOnSuccessListener {
                    state = BTNInterface.State.SYNC
                    onSuccess()
                }
                .addOnFailureListener {
                    state = BTNInterface.State.ERROR
                    onFailure()
                }
    }

    fun upload(onSuccess: () -> Boolean, onFailure: () -> Boolean)
    {
        state = BTNInterface.State.DOWNLOAD
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("${AccountHelper.uid}/$dirName.btn")
        val uri = Uri.fromFile(File(dirPath))
        reference.putFile(uri)
                .addOnSuccessListener {
                    state = BTNInterface.State.SYNC
                    onSuccess()
                }
                .addOnFailureListener {
                    state = BTNInterface.State.ERROR
                    onFailure()
                }
        state = BTNInterface.State.SYNC
    }
}
