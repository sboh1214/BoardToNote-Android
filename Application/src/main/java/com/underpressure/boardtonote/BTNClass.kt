package com.underpressure.boardtonote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BTNClass(context: Context, dirName: String) {
    private var context: Context = context
    var DirName: String = dirName
    val OriginalPicture: Bitmap?  by lazy { GetOriginalPicture() }

    private fun GetOriginalPicture(): Bitmap? {
        return try {
            BitmapFactory.decodeFile(context.filesDir.absolutePath + "/" + DirName + "/" + "OriPic.jpg")
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
            null
        }
    }
}

fun toast(context: Context, string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}

fun makeDir(context: Context, name: String?): String {
    if (name == null) {
        val c: Calendar = Calendar.getInstance()
        val d = SimpleDateFormat("yyMMdd-hhmmss")
        val dirName = d.format(c.time)
        val dirPath = context.filesDir.absolutePath + "/" + dirName
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dirName
    } else {
        val dirName = name
        val dirPath = context.filesDir.absolutePath + "/" + dirName
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dirName
    }
}

