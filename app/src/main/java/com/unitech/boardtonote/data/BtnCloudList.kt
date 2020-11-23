package com.unitech.boardtonote.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class BtnCloudList(val context: Context)
{
    private val cloudPath by lazy { "${context.filesDir.absolutePath}/cloud" }
    val cloudList: ArrayList<BtnCloud> by lazy { getDirList(context) }
    val tag = "BtnCloudList"

    private fun getDirList(context: Context): ArrayList<BtnCloud>
    {
        val arrayList = arrayListOf<BtnCloud>()
        File(cloudPath).listFiles()?.forEach {
            if (!it.isDirectory)
            {
                return@forEach
            }
            if (it.name.substringAfterLast('.').matches(Regex("btn\\d{12}")))
            {
                val dirName = it.name.substringBeforeLast(".")
                arrayList.add(BtnCloud(context, dirName))
            }
        }
        return arrayList
    }

    fun getDirListAsync(onSuccess: () -> Boolean)
    {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storage = FirebaseStorage.getInstance("gs://board-to-note.appspot.com")
        val listRef = storage.reference.child("user/$uid")
        listRef.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        val name = item.name.substringBeforeLast(".")
                        if (cloudList.find { btnCloudClass -> btnCloudClass.dirName == name } == null)
                        {
                            cloudList.add(BtnCloud(context, name))
                        }
                    }
                    onSuccess()
                }
                .addOnFailureListener {
                }
        return
    }

    fun rename(btnCloud: BtnCloud, name: String): Boolean
    {
        val srcDir = File(btnCloud.dirPath)
        val dstDir = File("$cloudPath/$name.btn")
        return if (dstDir.exists())
        {
            Log.w(tag, "rename failed (${srcDir.name} -> ${dstDir.name})")
            false
        }
        else
        {
            srcDir.renameTo(dstDir)
            btnCloud.dirName = name
            true
        }
    }

    fun delete(btnCloud: BtnCloud): Boolean
    {
        return try
        {
            val dir = File(btnCloud.dirPath)
            dir.deleteRecursively()
            dir.delete()
            cloudList.remove(btnCloud)
            btnCloud.delete()
            true
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            false
        }
    }

    fun moveFromLocal(localBtn: BtnLocalList, local: BtnLocal): BtnCloud
    {
        val cloudClass = BtnCloud(context, local.dirName!!)
        File(local.dirPath).copyRecursively(File(cloudClass.dirPath), true)
        File(local.dirPath).deleteRecursively()
        File(local.dirPath).delete()
        localBtn.delete(local)
        cloudList.add(cloudClass)
        cloudClass.uploadWithTimeStamp()
        return cloudClass
    }
}