package com.underpressure.boardtonote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BTNClass(context: Context, dirName: String)
{
    val context: Context = context
    val dirName: String = dirName
    private val dirPath: String by lazy { "${context.filesDir.absolutePath}/$dirName.btn" }
    val oriPic: Bitmap? by lazy { loadOriPic() }
    val oriPicPath: String by lazy { "$dirPath/OriPic.jpg" }
    private lateinit var state: State

    private fun loadOriPic(): Bitmap?
    {
        return try
        {
            BitmapFactory.decodeFile(dirPath + "/" + "OriPic.jpg")
        } catch (e: Exception)
        {
            Log.e("BTNClass", e.message ?: "Null")
            null
        }
    }

    private fun copyOriPic(inFile: File): Boolean
    {
        return try
        {
            val outFile = File(dirPath + "/" + "OriPic.jpg")
            val inStream = FileInputStream(inFile)
            val outStream = FileOutputStream(outFile)
            val inChannel = inStream.channel
            val outChannel = outStream.channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
            inStream.close()
            outStream.close()
            true
        } catch (e: Exception)
        {
            Log.e("BTNClass", e.message ?: "Null")
            false
        }
    }

    fun SaveDir()
    {

    }

    private fun CheckDir(dirName: String): Boolean
    {
        val dirPath = context.filesDir.absolutePath + "/" + dirName + ".btn"
        val dir = File(dirPath)
        return dir.exists()
    }

    companion object
    {
        enum class State
        {
            Never, Unsaved, Saved
        }

        fun makeDir(context: Context, name: String?): String?
        {
            if (name == null)
            {
                val c: Calendar = Calendar.getInstance()
                val d = SimpleDateFormat("yyMMdd-hhmmss", Locale.KOREA)
                val dirName = d.format(c.time)
                val dirPath = context.filesDir.absolutePath + "/" + dirName
                val dir = File(dirPath)
                if (!dir.exists())
                {
                    dir.mkdir()
                }
                return dirName
            } else
            {
                val dirName = name
                val dirPath = context.filesDir.absolutePath + "/" + dirName
                val dir = File(dirPath)
                return if (!dir.exists())
                {
                    null
                } else
                {
                    dir.mkdir()
                    dirName
                }
            }
        }
    }
}
