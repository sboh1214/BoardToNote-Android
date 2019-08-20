package com.unitech.boardtonote.helper

import android.content.Context
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import java.io.File

object ZipHelper
{
    fun zip(context: Context, path: String, title: String)
    {
        val zipFile = ZipFile("${context.cacheDir.path}/$title.zip")
        zipFile.addFolder(File(path), ZipParameters())
    }

    fun unzip(context: Context, src: String, dst: String)
    {
        val zipFile = ZipFile(src)
        zipFile.extractAll(dst)
    }
}