package com.unitech.boardtonote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.perf.FirebasePerformance
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * A Class for Board To Note Project File
 */
class BTNClass(private val context: Context, var dirName: String?, val location: Location)
{
    private val tag = "BTNClass"

    enum class Location(val value: Int)
    {
        LOCAL(1), FIREBASE_STORAGE(2)
    }

    data class ContentClass
    (
            var text: String?,
            var blockList: ArrayList<BlockClass>
    )

    data class BlockClass
    (
            val text: String,
            val confidence: Float?,
            val language: List<String?>,
            @JsonIgnore
            val frame: Rect?,
            val lines: List<LineClass>
    )

    data class LineClass
    (
            val text: String,
            val confidence: Float?,
            val language: List<String?>,
            @JsonIgnore
            val frame: Rect?,
            val lines: List<ElementClass>
    )

    data class ElementClass
    (
            val text: String,
            val confidence: Float?,
            val language: List<String?>,
            @JsonIgnore
            val frame: Rect?
    )

    init
    {
        if (!File(parentDirPath).exists())
        {
            File(parentDirPath).mkdir()
        }
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

    private val parentDirPath: String
        get()
        {
            return when (location)
            {
                Location.LOCAL            -> "${context.filesDir.path}/local"
                Location.FIREBASE_STORAGE -> "${context.filesDir.path}/firebase_storage"
            }
        }

    private val oriPic: Bitmap?
        get()
        {
            return try
            {
                BitmapFactory.decodeFile(oriPicPath)
            }
            catch (e: Exception)
            {
                Log.e(tag, e.toString())
                null
            }
        }

    private val dirPath: String
        get()
        {
            return "$parentDirPath/$dirName.btn"
        }

    val oriPicPath: String
        get()
        {
            return "$dirPath/OriPic.jpg"
        }

    private val contentPath: String
        get()
        {
            return "$dirPath/content.json"
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
            Log.e(tag, e.toString())
            false
        }
    }

    fun decodeOriPic(width: Int, height: Int?): Bitmap?
    {
        try
        {
            val options = BitmapFactory.Options()
            options.outWidth = width
            if (height != null)
            {
                options.outHeight = height
            }
            return BitmapFactory.decodeFile(oriPicPath, options)
        }
        catch (e: Exception)
        {

        }

        return null
    }

    private fun makeLocalDir(name: String?)
    {
        if (name == null)
        {
            val c: Calendar = Calendar.getInstance()
            val d = SimpleDateFormat("yyMMdd-hhmmss", Locale.KOREA)
            dirName = d.format(c.time)
            val dirPath = context.filesDir.absolutePath + "/local/" + dirName + ".btn"
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
            var dir = File(context.filesDir.absolutePath + "/local/" + dirName + ".btn")
            if (!dir.exists())
            {
                dir.mkdir()
                return
            }
            var num = 1
            while (true)
            {
                dirName = name + num.toString()
                dir = File(context.filesDir.absolutePath + "/local/" + dirName + ".btn")
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
            Log.w(tag, "rename failed (${srcDir.name} -> ${dstDir.name})")
            false
        }
        else
        {
            srcDir.renameTo(dstDir)
            dirName = name
            Log.i(tag, "rename succeeded (${srcDir.name} -> ${dstDir.name})")
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
            Log.e(tag, e.toString())
            false
        }
    }

    lateinit var content: ContentClass

    fun asyncGetContent(onSuccess: (ContentClass) -> Boolean, onFailure: (ContentClass) -> Boolean)
    {
        if (!File(contentPath).exists())
        {
            File(contentPath).canWrite()
            analyze(onSuccess, onFailure)
        }
        else
        {
            val mapper = jacksonObjectMapper()
            content = mapper.readValue(File(contentPath))
            onSuccess(content)
        }
    }

    private fun analyze(onSuccess: (ContentClass) -> Boolean, onFailure: (ContentClass) -> Boolean)
    {
        if (oriPic == null)
        {
            return
        }
        val trace = FirebasePerformance.getInstance().newTrace("process_image")
        trace.start()
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(oriPic!!)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image).apply {
            addOnSuccessListener { firebaseVisionText ->
                try
                {
                    trace.stop()
                    saveVisionText(firebaseVisionText)
                    Log.i(tag, "analyze() Success $dirName")
                    Log.v(tag, firebaseVisionText.text.replace("\n", " "))
                    onSuccess(content)
                }
                catch (e: Exception)
                {
                    onFailure(content)
                    Log.e(tag, "analyze() Exception $dirName")
                }
            }
            addOnFailureListener { e ->
                trace.stop()
                Log.i(tag, "analyze() Failure $dirName")
                Log.w(tag, e.toString())
            }
        }
        return
    }

    private fun saveVisionText(visionText: FirebaseVisionText)
    {
        val list = arrayListOf<BlockClass>()
        for (b in visionText.textBlocks)
        {
            Log.v(tag, "saveVisionText block ${b.text.replace("\n", " ")}")
            val lines = arrayListOf<LineClass>()
            for (l in b.lines)
            {
                Log.v(tag, "saveVisionText line ${l.text.replace("\n", " ")}")
                val elements = arrayListOf<ElementClass>()
                for (e in l.elements)
                {
                    Log.v(tag, "saveVisionText block ${e.text.replace("\n", " ")}")
                    val elementClass = ElementClass(e.text, e.confidence, e.recognizedLanguages.map { lang -> lang.languageCode }, e.boundingBox)
                    elements.add(elementClass)
                }
                val lineClass = LineClass(l.text, l.confidence, l.recognizedLanguages.map { lang -> lang.languageCode }, l.boundingBox, elements)
                lines.add(lineClass)
            }
            val blockClass = BlockClass(b.text, b.confidence, b.recognizedLanguages.map { lang -> lang.languageCode }, b.boundingBox, lines)
            list.add(blockClass)
        }
        content = ContentClass(visionText.text, list)
        try
        {
            val mapper = jacksonObjectMapper()
            mapper.writerWithDefaultPrettyPrinter().writeValue(File(contentPath), content)
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
        }
    }

    companion object
    {
        fun toLocate(int: Int): Location
        {
            return when (int)
            {
                Location.LOCAL.value            -> Location.LOCAL
                Location.FIREBASE_STORAGE.value -> Location.FIREBASE_STORAGE
                else                            -> Location.LOCAL
            }
        }
    }
}

