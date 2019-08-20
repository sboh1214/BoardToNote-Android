package com.unitech.boardtonote.helper

import android.content.Context
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import java.io.File

object ZipHelper
{
    fun zip(context: Context, path: String, title: String)
    {
        val zipFile = ZipFile(File("${context.cacheDir.path}/$title.zip"))
        zipFile.addFolder(path, ZipParameters())
    }

    fun unzip(context: Context, path: String)
    {

    }
}