package com.unitech.boardtonote.data

import android.content.Context
import android.util.Log
import java.io.File

class ListLocalClass(val context: Context)
{
    private val tag = "ListLocalClass"

    val localList by lazy { getDirList(context) }
    private val localPath by lazy { "${context.filesDir.absolutePath}/local" }

    private fun getDirList(context: Context): ArrayList<BTNLocalClass>
    {
        val arrayList = arrayListOf<BTNLocalClass>()
        File(localPath).listFiles()?.forEach {
            if (!it.isDirectory)
            {
                return@forEach
            }
            if (it.name.substringAfterLast('.') == "btn")
            {
                val dirName = it.name.substringBeforeLast('.')
                arrayList.add(BTNLocalClass(context, dirName))
            }
        }
        return arrayList
    }

    fun rename(btnLocalClass: BTNLocalClass, name: String): Boolean
    {
        val srcDir = File(btnLocalClass.dirPath)
        val dstDir = File("$localPath/$name.btn")
        return if (dstDir.exists())
        {
            Log.w(tag, "rename failed (${srcDir.name} -> ${dstDir.name})")
            false
        }
        else
        {
            srcDir.renameTo(dstDir)
            btnLocalClass.dirName = name
            Log.i(tag, "rename succeeded (${srcDir.name} -> ${dstDir.name})")
            true
        }
    }

    fun delete(btnLocalClass: BTNLocalClass): Boolean
    {
        return try
        {
            val dir = File(btnLocalClass.dirPath)
            dir.deleteRecursively()
            localList.remove(btnLocalClass)
            true
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            false
        }
    }
}