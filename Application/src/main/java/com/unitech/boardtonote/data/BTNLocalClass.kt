package com.unitech.boardtonote.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

/**
 * A Class for Board To Note Project File
 */
class BTNLocalClass(override val context: Context, override var dirName: String?) : BTNInterface
{
    override val location = BTNInterface.Location.LOCAL

    override val oriPic: Bitmap? by lazy {
        try
        {
            BitmapFactory.decodeFile(oriPicPath)
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            null
        }
    }
    override val tag = "BTNLocalClass"

    init
    {

        if (!File(parentDirPath).exists())
        {
            File(parentDirPath).mkdir()
        }
        // make local directory if it does not exist
        if (dirName == null)
        {
            makeLocalDir(null)
        }
        else if (!File(dirPath).exists())
        {
            makeLocalDir(dirName)
        }
    }

    override lateinit var content: BTNInterface.ContentClass

    override val parentDirPath: String
        get()
        {
            return "${context.filesDir.path}/local"
        }
}

