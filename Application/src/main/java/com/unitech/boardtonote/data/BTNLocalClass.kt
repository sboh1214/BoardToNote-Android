package com.unitech.boardtonote.data

import android.content.Context
import java.io.File

/**
 * A Class for Board To Note Project File
 */
class BTNLocalClass(override val context: Context, override var dirName: String?) : BTNInterface
{
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

