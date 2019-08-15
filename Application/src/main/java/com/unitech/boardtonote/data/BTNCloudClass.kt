package com.unitech.boardtonote.data

import android.content.Context
import java.io.File

class BTNCloudClass(override val context: Context, override var dirName: String?) : BTNInterface
{
    override val tag = "BTNCloudClass"

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
        get() = "${context.filesDir.path}/cloud"

    override val dirPath: String
        get() = "$parentDirPath/$dirName.btn"

    override fun rename(name: String): Boolean
    {
        return super.rename(name)
    }
}
