package com.unitech.boardtonote.data

import android.content.Context
import android.util.Log
import java.io.File

class BtnLocalList(val context: Context)
{
    private val tag = "BtnLocalList"

    val localList by lazy { getDirList(context) }
    private val localPath by lazy { "${context.filesDir.absolutePath}/local" }

    private fun getDirList(context: Context): ArrayList<BtnLocal>
    {
        val arrayList = arrayListOf<BtnLocal>()
        File(localPath).listFiles()?.forEach {
            if (!it.isDirectory)
            {
                return@forEach
            }
            if (it.name.substringAfterLast('.') == "btn")
            {
                val dirName = it.name.substringBeforeLast('.')
                arrayList.add(BtnLocal(context, dirName))
            }
        }
        return arrayList
    }

    fun rename(btnLocal: BtnLocal, name: String): Boolean
    {
        val srcDir = File(btnLocal.dirPath)
        val dstDir = File("$localPath/$name.btn")
        return if (dstDir.exists())
        {
            Log.w(tag, "rename failed (${srcDir.name} -> ${dstDir.name})")
            false
        }
        else
        {
            srcDir.renameTo(dstDir)
            btnLocal.dirName = name
            true
        }
    }

    fun delete(btnLocal: BtnLocal): Boolean
    {
        return try
        {
            val dir = File(btnLocal.dirPath)
            dir.deleteRecursively()
            dir.delete()
            localList.remove(btnLocal)
            true
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            false
        }
    }
}