package com.unitech.boardtonote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "BTNClass"

/**
 * A Class for Board To Note Project File
 */
class BTNClass(private val context: Context, var dirName: String)
{

    private val dirPath: String
        get()
        {
            return "${context.filesDir.absolutePath}/$dirName.btn"
        }

    val oriPic: Bitmap?
        get()
        {
            return loadOriPic()
        }

    val oriPicPath: String
        get()
        {
            return "$dirPath/OriPic.jpg"
        }

    var visionText: FirebaseVisionText? = null

    /**
     * @return Bitmap of original picture.
     * @exception[Exception] If original picture doesn't exist.
     */
    private fun loadOriPic(): Bitmap?
    {
        return try
        {
            BitmapFactory.decodeFile("$dirPath/OriPic.jpg")
        }
        catch (e: Exception)
        {
            Log.e(TAG, e.toString())
            null
        }
    }

    fun copyOriPic(uri: Uri): Boolean
    {
        return try
        {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(File(oriPicPath))
            inputStream?.copyTo(outputStream, DEFAULT_BUFFER_SIZE)
            inputStream?.close()
            outputStream.close()
            true
        }
        catch (e: Exception)
        {
            Log.e(TAG, e.toString())
            false
        }
    }

    fun rename(name: String): Boolean
    {
        val srcDir = File(dirPath)
        val dstDir = File("${context.filesDir.absolutePath}/$name.btn")
        return if (dstDir.exists())
        {
            Log.w(TAG, "rename failed (${srcDir.name} -> ${dstDir.name})")
            false
        }
        else
        {
            srcDir.renameTo(dstDir)
            dirName = name
            Log.i(TAG, "rename succeeded (${srcDir.name} -> ${dstDir.name})")
            true
        }
    }

    fun delete(): Boolean
    {
        return try
        {
            val dir = File(dirPath)
            dir.deleteRecursively()
            true
        }
        catch (e: Exception)
        {
            Log.e(TAG, e.toString())
            false
        }
    }

    companion object
    {
        fun makeDir(context: Context, name: String?): String
        {
            if (name == null)
            {
                val c: Calendar = Calendar.getInstance()
                val d = SimpleDateFormat("yyMMdd-hhmmss", Locale.KOREA)
                val dirName = d.format(c.time)
                val dirPath = context.filesDir.absolutePath + "/" + dirName + ".btn"
                val dir = File(dirPath)
                if (!dir.exists())
                {
                    dir.mkdir()
                }
                return dirName
            }
            else
            {
                var dirName = name
                var dir = File(context.filesDir.absolutePath + "/" + dirName + ".btn")
                if (!dir.exists())
                {
                    dir.mkdir()
                    return dirName
                }
                var num = 1
                while (true)
                {
                    dirName = name + num.toString()
                    dir = File(context.filesDir.absolutePath + "/" + dirName + ".btn")
                    if (!dir.exists())
                    {
                        dir.mkdir()
                        return dirName
                    }
                    num++
                }
            }
        }
    }
}
