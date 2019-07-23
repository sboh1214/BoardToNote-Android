package com.unitech.boardtonote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "BTNClass"

/**
 * A Class for Board To Note Project File
 */
class BTNClass(context: Context, dirName: String)
{
    private val context: Context = context
    val dirName: String = dirName
    private val dirPath: String by lazy { "${context.filesDir.absolutePath}/$dirName.btn" }
    val oriPic: Bitmap? by lazy { loadOriPic() }
    val oriPicPath: String by lazy { "$dirPath/OriPic.jpg" }
    val visionText: FirebaseVisionText? = null
    private lateinit var state: State

    fun analyzePic()
    {
        val image: FirebaseVisionImage = FirebaseVisionImage.fromFilePath(context, Uri.parse(oriPicPath))
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val result = detector.processImage(image).result
        for (block in result!!.textBlocks!!)
        {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines)
            {
                val lineText = line.text
                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements)
                {
                    val elementText = element.text
                    val elementConfidence = element.confidence
                    val elementLanguages = element.recognizedLanguages
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
    }

    /**
     * @return Bitmap of original picture.
     * @exception[Exception] If original picture doesn't exist.
     */
    private fun loadOriPic(): Bitmap?
    {
        return try
        {
            BitmapFactory.decodeFile("$dirPath/OriPic.jpg")
        } catch (e: Exception)
        {
            Log.e(TAG, e.toString())
            null
        }
    }

    private fun copyOriPic(inFile: File): Boolean
    {
        return try
        {
            val outFile = File("$dirPath/OriPic.jpg")
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
            Log.e(TAG, e.message ?: "Null")
            false
        }
    }

    companion object
    {
        enum class State
        {
            Never, Unsaved, Saved
        }

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
            } else
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
