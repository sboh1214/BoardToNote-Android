package com.unitech.boardtonote.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.unitech.boardtonote.Constant
import java.io.File

class BtnLocal(override val context: Context, override var dirName: String?) : BtnInterface
{
    override var onLocationAndState: (Int, Int?) -> Boolean = { _, _ -> true }
    override val dirPath: String
        get() = "$parentDirPath/$dirName.btn"
    override val location = Constant.locationLocal
    override val state: Int? = null

    override val oriPic: Bitmap?
        get() = BitmapFactory.decodeFile(oriPicPath)
    override val tag = "BtnLocal"

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

    override var content: BtnInterface.ContentClass? = null
        get()
        {
            return if (field == null)
            {
                BtnInterface.ContentClass(arrayListOf())
            }
            else
            {
                field
            }
        }

    override val parentDirPath: String
        get()
        {
            return "${context.filesDir.path}/local"
        }
}

