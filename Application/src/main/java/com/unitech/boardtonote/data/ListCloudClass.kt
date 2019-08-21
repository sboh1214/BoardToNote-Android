package com.unitech.boardtonote.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ListCloudClass(val context: Context)
{
    private val cloudPath by lazy { "${context.filesDir.absolutePath}/cloud" }
    val cloudList: ArrayList<BTNCloudClass> by lazy { getDirList(context) }
    val tag = "ListCloudClass"

    private fun getDirList(context: Context): ArrayList<BTNCloudClass>
    {
        val arrayList = arrayListOf<BTNCloudClass>()
        File(cloudPath).listFiles()?.forEach {
            if (!it.isDirectory)
            {
                return@forEach
            }
            if (it.name.substringAfterLast('.').matches(Regex("btn\\d{12}")))
            {
                val dirName = it.name.substringBeforeLast(".")
                arrayList.add(BTNCloudClass(context, dirName))
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
                            cloudList.add(BTNCloudClass(context, name))
                        }
                    }
                    onSuccess()
                }
                .addOnFailureListener {
                }
        return
    }

    fun rename(btnCloudClass: BTNCloudClass, name: String): Boolean
    {
        val srcDir = File(btnCloudClass.dirPath)
        val dstDir = File("$cloudPath/$name.btn")
        return if (dstDir.exists())
        {
            Log.w(tag, "rename failed (${srcDir.name} -> ${dstDir.name})")
            false
        }
        else
        {
            srcDir.renameTo(dstDir)
            btnCloudClass.dirName = name
            Log.i(tag, "rename succeeded (${srcDir.name} -> ${dstDir.name})")
            true
        }
    }

    fun delete(btnCloudClass: BTNCloudClass): Boolean
    {
        return try
        {
            val dir = File(btnCloudClass.dirPath)
            dir.deleteRecursively()
            dir.delete()
            cloudList.remove(btnCloudClass)
            btnCloudClass.delete()
            true
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            false
        }
    }

    fun moveFromLocal(localList: ListLocalClass, localClass: BTNLocalClass): BTNCloudClass
    {
        val cloudClass = BTNCloudClass(context, localClass.dirName!!)
        File(localClass.dirPath).copyRecursively(File(cloudClass.dirPath), true)
        File(localClass.dirPath).deleteRecursively()
        File(localClass.dirPath).delete()
        localList.delete(localClass)
        cloudList.add(cloudClass)
        cloudClass.uploadWithTimeStamp()
        return cloudClass
    }
}