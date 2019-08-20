package com.unitech.boardtonote.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ListCloudClass(val context: Context)
{
    private val cloudPath = "${context.filesDir.absolutePath}/cloud"
    val dirList: ArrayList<BTNCloudClass> by lazy { getDirList(context) }

    private fun getDirList(context: Context): ArrayList<BTNCloudClass>
    {
        val arrayList = arrayListOf<BTNCloudClass>()
        File(cloudPath).listFiles()?.forEach {
            if (!it.isDirectory)
            {
                return@forEach
            }
            if (it.name.substringAfterLast('.') == "btn")
            {
                val dirName = it.name.substringBeforeLast('.')
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
                        item.getFile(File("$cloudPath/${item.name}"))
                    }

                    File(cloudPath).listFiles()?.forEach {
                        if (!it.isDirectory && it.name.substringAfterLast('.') == "btn")
                        {
                            val dirName = it.name.substringBeforeLast('.')
                            dirList.add(BTNCloudClass(context, dirName))
                        }
                    }
                    onSuccess()
                }
                .addOnFailureListener {
                }
        return
    }

    fun moveFromLocal(localClass: BTNLocalClass): BTNCloudClass
    {
        File(localClass.dirPath).copyRecursively(File(localClass.dirPath), true)
        File(localClass.dirPath).deleteRecursively()
        val cloudClass = BTNCloudClass(context, localClass.dirName)
        dirList.add(cloudClass)
        cloudClass.upload()
        return cloudClass
    }
}