package com.unitech.boardtonote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.perf.FirebasePerformance
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "BTNClass"

/**
 * A Class for Board To Note Project File
 */
class BTNClass(private val context: Context, var dirName: String?, location: Location)
{
    enum class Location
    {
        LOCAL, FIREBASE_STORAGE
    }

    data class ContentClass
    (
            var text: String?,
            var blockList: List<BlockClass>
    )

    data class BlockClass
    (
            val text: String,
            val confidence: Float?,
            val language: List<String?>,
            val frame: Rect?,
            val lines: List<LineClass>
    )

    data class LineClass
    (
            val text: String,
            val confidence: Float?,
            val language: List<String?>,
            val frame: Rect?,
            val lines: List<ElementClass>
    )

    data class ElementClass
    (
            val text: String,
            val confidence: Float?,
            val language: List<String?>,
            val frame: Rect?
    )

    init
    {
        when (location)
        {
            Location.LOCAL            ->
            {
                // make local directory if it does not exist
                if (dirName == null)
                {
                    makeLocalDir(null)
                }
                else if (!File(dirPath).exists())
                {
                    makeLocalDir(dirName)
                }

                //make json file if it does not exist

            }
            Location.FIREBASE_STORAGE ->
            {

            }
        }
    }

    private var content = ContentClass(null, mutableListOf())

    val oriPic: Bitmap?
        get()
        {
            return loadOriPic()
        }


    private val dirPath: String
        get()
        {
            return "${context.filesDir.absolutePath}/$dirName.btn"
        }

    val oriPicPath: String
        get()
        {
            return "$dirPath/OriPic.jpg"
        }

    val contentPath: String
        get()
        {
            return "$dirPath/content.json"
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

    private fun makeLocalDir(name: String?)
    {
        if (name == null)
        {
            val c: Calendar = Calendar.getInstance()
            val d = SimpleDateFormat("yyMMdd-hhmmss", Locale.KOREA)
            dirName = d.format(c.time)
            val dirPath = context.filesDir.absolutePath + "/" + dirName + ".btn"
            val dir = File(dirPath)
            if (!dir.exists())
            {
                dir.mkdir()
            }
            return
        }
        else
        {
            dirName = name
            var dir = File(context.filesDir.absolutePath + "/" + dirName + ".btn")
            if (!dir.exists())
            {
                dir.mkdir()
                return
            }
            var num = 1
            while (true)
            {
                dirName = name + num.toString()
                dir = File(context.filesDir.absolutePath + "/" + dirName + ".btn")
                if (!dir.exists())
                {
                    dir.mkdir()
                    return
                }
                num++
            }
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

    fun analyze(onSuccess: (FirebaseVisionText) -> Boolean, onFailure: (java.lang.Exception) -> Boolean): Boolean
    {
        if (oriPic == null)
        {
            return false
        }
        val trace = FirebasePerformance.getInstance().newTrace("process_image")
        trace.start()
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(oriPic!!)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image).apply {
            addOnSuccessListener { firebaseVisionText ->
                trace.stop()
                saveVisionText(firebaseVisionText)
                Log.i(TAG, "analyze() Success $dirName")
                Log.v(TAG, firebaseVisionText.text)
                onSuccess(firebaseVisionText)
            }
            addOnFailureListener { e ->
                trace.stop()
                Log.i(TAG, "analyze() Failure $dirName")
                Log.w(TAG, e.toString())
                onFailure(e)
            }
        }
        return true
    }

    private fun saveVisionText(visionText: FirebaseVisionText)
    {
        val blocks = mutableListOf<BlockClass>()
        for (b in visionText.textBlocks)
        {
            val lines = mutableListOf<LineClass>()
            for (l in b.lines)
            {
                val elements = mutableListOf<ElementClass>()
                for (e in l.elements)
                {
                    val elementClass = ElementClass(e.text, e.confidence, e.recognizedLanguages.map { lang -> lang.languageCode }, e.boundingBox)
                    elements + elementClass
                }
                val lineClass = LineClass(l.text, l.confidence, l.recognizedLanguages.map { lang -> lang.languageCode }, l.boundingBox, elements)
                lines + lineClass
            }
            val blockClass = BlockClass(b.text, b.confidence, b.recognizedLanguages.map { lang -> lang.languageCode }, b.boundingBox, lines)
            blocks+blockClass
        }
        content.text = visionText.text
        content.blockList = blocks
        val mapper = jacksonObjectMapper()
        mapper.writerWithDefaultPrettyPrinter().writeValue(File(contentPath), content)
    }
}
