package com.unitech.boardtonote.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.ZipHelper
import java.io.File

class BTNCloudClass(override val context: Context, override var dirName: String?) : BTNInterface
{
    override val location by lazy { Constant.locationCloud }

    override val oriPic: Bitmap? by lazy {
        try
        {
            BitmapFactory.decodeFile(oriPicPath)
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            null
        }
    }
    override val tag = "BTNCloudClass"

    private val firebaseUrl = "gs://board-to-note.appspot.com/"

    var onState: (Int) -> Boolean = { true }
    var state: Int = Constant.stateUnknown
        set(value)
        {
            field = value
            onState(value)
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

    fun download()
    {
        state = Constant.stateDownload
        Log.d(tag, "Downloading...")
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("${AccountHelper.uid}/$dirName.btn")
        val file = File(dirPath)
        reference.getFile(file)
                .addOnSuccessListener {
                    state = Constant.stateSync
                    Log.d(tag, "Download Success")
                }
                .addOnFailureListener {
                    state = Constant.stateError
                    Log.d(tag, "Download Failed")
                    Log.e(tag, it.toString())
                }
    }

    fun upload()
    {
        state = Constant.stateUpload
        Log.d(tag, "Uploading...")
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.btn")
        ZipHelper.zip(context, dirPath, dirName ?: "temp")
        val uri = Uri.fromFile(File("${context.cacheDir.path}/${dirName ?: "temp"}.zip"))
        reference.putFile(uri)
                .addOnSuccessListener {
                    state = Constant.stateSync
                    Log.d(tag, "Upload Success")
                }
                .addOnFailureListener {
                    state = Constant.stateError
                    Log.d(tag, "Upload Failed")
                    Log.e(tag, it.toString())
                }
    }

    fun delete()
    {
        state = Constant.stateDelete
        Log.d(tag, "Deleting...")
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.btn")
        reference.delete()
                .addOnSuccessListener {
                    state = Constant.stateSync
                    Log.d(tag, "Delete Success")
                }
                .addOnFailureListener {
                    state = Constant.stateError
                    Log.d(tag, "Delete Failed")
                }
    }
}
