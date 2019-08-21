package com.unitech.boardtonote.helper

import android.content.Context
import net.lingala.zip4j.ZipFile
import java.io.File

object ZipHelper
{
    fun zip(context: Context, path: String, title: String): String
    {
        val zipFile = ZipFile("${context.cacheDir.path}/$title.zip")
        zipFile.addFile(File("$path/OriPic.jpg"))
        zipFile.addFile(File("$path/content.json"))
        return "${context.cacheDir.path}/$title.zip"
    }

    fun unzip(src: String, dst: String)
    {
        val zipFile = ZipFile(src)
        zipFile.extractFile("OriPic.jpg", dst)
        zipFile.extractFile("content.json", dst)
    }
}