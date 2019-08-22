package com.unitech.boardtonote.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.unitech.boardtonote.Constant
import com.unitech.boardtonote.helper.AccountHelper
import com.unitech.boardtonote.helper.ZipHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BTNCloudClass(override val context: Context, override var dirName: String?) : BTNInterface
{
    override val location by lazy { Constant.locationCloud }

    override val dirPath: String
        get() = "$parentDirPath/$dirName.btn$localTimeStamp"

    private var localTimeStamp: String = "000000000000"

    private fun applyLocalTimeStamp()
    {
        Log.d(tag, "$dirName : applyLocalTimeStamp : $localTimeStamp")
        val src = File(parentDirPath).listFiles()?.find { file -> file.name.substringBeforeLast(".") == dirName }
        val dst = File("$parentDirPath/$dirName.btn$localTimeStamp")
        src?.renameTo(dst)
    }

    private var cloudTimeStamp: String = "000000000000"

    private fun applyCloudTimestamp()
    {
        val meta = StorageMetadata.Builder().setCustomMetadata(Constant.timestamp, cloudTimeStamp).build()
        FirebaseStorage
                .getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.zip").updateMetadata(meta)
                .addOnSuccessListener {
                    Log.d(tag, "$dirName : Cloud MetaData Update Success : $cloudTimeStamp")
                }
                .addOnFailureListener {
                    Log.d(tag, "$dirName : Cloud MetaData Update Failed : $cloudTimeStamp")
                    Log.e(tag, it.toString())
                }
    }

    override val tag = "BTNCloudClass"
    private val firebaseUrl = "gs://board-to-note.appspot.com/"

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
    override var onLocationAndState: (Int, Int?) -> Boolean = { _, _ -> true }
    override var state: Int = Constant.stateUnknown
        set(value)
        {
            field = value
            onLocationAndState(location, value)
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
            makeCloudDir(null)
        }
        val find = File(parentDirPath).listFiles()?.find { file -> file.name.substringBeforeLast(".") == dirName }
        if (find == null)
        {
            makeCloudDir(dirName)
        }

        localTimeStamp = File(parentDirPath).listFiles()!!.find { it.nameWithoutExtension == dirName }?.name?.substringAfterLast(".")?.drop(3)!!
        Log.d(tag, "$dirName : init localTimeStamp : $localTimeStamp")

        Log.d(tag, "$dirName : try to initialize cloud Timestamp")
        FirebaseStorage
                .getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.zip").metadata
                .addOnSuccessListener {
                    cloudTimeStamp = it.getCustomMetadata(Constant.timestamp)
                    Log.d(tag, "$dirName : init cloudTimeStamp : $cloudTimeStamp")
                    when
                    {
                        localTimeStamp > cloudTimeStamp -> upload()
                        localTimeStamp < cloudTimeStamp -> download()
                        else                            -> state = Constant.stateSync
                    }
                }
                .addOnFailureListener {
                    Log.d(tag, "$dirName : failed to init cloudTimeStamp : $cloudTimeStamp")
                    Log.e(tag, it.toString())
                }
    }

    override lateinit var content: BTNInterface.ContentClass

    override val parentDirPath: String
        get() = "${context.filesDir.path}/cloud"

    private fun download()
    {
        state = Constant.stateDownload
        Log.d(tag, "$dirName : Downloading...")
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.zip")
        val file = File("${context.cacheDir}/$dirName.zip")
        reference.getFile(file)
                .addOnSuccessListener {
                    localTimeStamp = cloudTimeStamp
                    applyLocalTimeStamp()
                    ZipHelper.unzip(file.path, dirPath)
                    state = Constant.stateSync
                    Log.d(tag, "$dirName : Download Success")
                }
                .addOnFailureListener {
                    state = Constant.stateError
                    Log.d(tag, "Download Failed")
                    Log.e(tag, it.toString())
                }
    }

    private fun upload()
    {
        state = Constant.stateUpload
        Log.d(tag, "Uploading...")
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.zip")
        val path = ZipHelper.zip(context, dirPath, dirName!!)
        val uri = Uri.fromFile(File(path))
        reference.putFile(uri)
                .addOnSuccessListener {
                    state = Constant.stateSync
                    cloudTimeStamp = localTimeStamp
                    applyCloudTimestamp()
                    Log.d(tag, "Upload Success")
                }
                .addOnFailureListener {
                    state = Constant.stateError
                    Log.d(tag, "Upload Failed")
                    Log.e(tag, it.toString())
                }
    }

    fun uploadWithTimeStamp()
    {
        val stamp = SimpleDateFormat("yyMMddhhmmss", Locale.US)
        localTimeStamp = stamp.format(Date().time)
        applyLocalTimeStamp()
        state = Constant.stateUpload
        Log.d(tag, "Uploading...")
        val reference = FirebaseStorage.getInstance(firebaseUrl).reference
                .child("user/${AccountHelper.uid}/$dirName.zip")
        val path = ZipHelper.zip(context, dirPath, dirName!!)
        val uri = Uri.fromFile(File(path))
        reference.putFile(uri)
                .addOnSuccessListener {
                    state = Constant.stateSync
                    cloudTimeStamp = localTimeStamp
                    applyCloudTimestamp()
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
                .child("user/${AccountHelper.uid}/$dirName.zip")
        reference.delete()
                .addOnSuccessListener {
                    state = Constant.stateSync
                    File(dirPath).deleteRecursively()
                    File(dirPath).delete()
                    Log.d(tag, "Delete Success")
                }
                .addOnFailureListener {
                    state = Constant.stateError
                    Log.d(tag, "Delete Failed")
                }
    }

    private fun makeCloudDir(name: String?)
    {
        val stamp = SimpleDateFormat("yyMMddhhmmss", Locale.US)
        val timestamp = stamp.format(Date().time)
        if (name == null)
        {
            val c: Calendar = Calendar.getInstance()
            val d = SimpleDateFormat("yyMMdd-hhmmss", Locale.getDefault())
            dirName = d.format(c.time)
            val dirPath = "$parentDirPath/$dirName.btn$timestamp"
            val dir = File(dirPath)
            if (!dir.exists())
            {
                dir.mkdir()
            }
            Log.d(tag, "Create : $dirName $timestamp")
            localTimeStamp = timestamp
            return
        }
        else
        {
            dirName = name
            var dir = File("$parentDirPath/$dirName.btn$localTimeStamp")
            if (!dir.exists())
            {
                dir.mkdir()
                Log.d(tag, "$dirName : Create : $localTimeStamp")
                return
            }
            var num = 1
            while (true)
            {
                dirName = name + num.toString()
                dir = File("$parentDirPath/$dirName.btn$localTimeStamp")
                if (!dir.exists())
                {
                    dir.mkdir()
                    Log.d(tag, "$dirName : Create : $localTimeStamp")
                    return
                }
                num++
            }
        }
    }

    override fun saveContent()
    {
        super.saveContent()
        uploadWithTimeStamp()
    }
}
