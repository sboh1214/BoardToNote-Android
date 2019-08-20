package com.unitech.boardtonote.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.ZipHelper
import java.io.File

class BTNCloudClass(override val context: Context, override var dirName: String?) : BTNInterface
{
    override val tag = "BTNCloudClass"

    private val firebaseUrl = "gs://board-to-note.appspot.com/"

    var onState: (State) -> Boolean = { true }
    private var state: State = State.UNKNOWN
        set(value)
        {
            field = value
            onState(value)
        }

    enum class State(val value: Int)
    {
        SYNC(0),
        DOWNLOAD(1),
        UPLOAD(2),
        LOCAL(3),
        ONLINE(4),
        ERROR(5),
        UNKNOWN(6)
    }

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
        state = State.DOWNLOAD
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("${AccountHelper.uid}/$dirName.btn")
        val file = File(dirPath)
        reference.getFile(file)
                .addOnSuccessListener { state = State.SYNC }
                .addOnFailureListener { state = State.ERROR }
    }

    fun upload()
    {
        state = State.UPLOAD
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.btn")
        ZipHelper.zip(context, dirPath, dirName ?: "temp")
        val uri = Uri.fromFile(File("${context.cacheDir.path}/${dirName ?: "temp"}.zip"))
        reference.putFile(uri)
                .addOnSuccessListener {
                    state = State.SYNC
                    Log.d(tag, "Upload Success")
                }
                .addOnFailureListener {
                    state = State.ERROR
                    Log.d(tag, "Upload Failed")
                }
        state = State.SYNC
    }
}
