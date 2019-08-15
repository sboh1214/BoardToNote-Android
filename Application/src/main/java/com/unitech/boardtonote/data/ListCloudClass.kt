package com.unitech.boardtonote.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ListCloudClass(val context: Context)
{
    private val dirPath = "${context.filesDir.absolutePath}/cloud"
    val dirList: ArrayList<BTNCloudClass> = arrayListOf()

    fun getDirListAsync(onSuccess: () -> Boolean)
    {
        val list = arrayListOf<BTNCloudClass>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storage = FirebaseStorage.getInstance("gs://board-to-note.appspot.com")
        val listRef = storage.reference.child("user/$uid")
        listRef.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        item.getFile(File("$dirPath/${item.name}"))
                    }

                    File(dirPath).listFiles()?.forEach {
                        if (!it.isDirectory && it.name.substringAfterLast('.') == "btn")
                        {
                            val dirName = it.name.substringBeforeLast('.')
                            list.add(BTNCloudClass(context, dirName))
                        }
                    }
                    onSuccess()
                }
                .addOnFailureListener {
                }
        return
    }
}