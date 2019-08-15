package com.unitech.boardtonote.data

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.perf.FirebasePerformance
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipOutputStream

interface BTNInterface
{
    val tag: String

    val context: Context
    var dirName: String?

    val parentDirPath: String
    val dirPath: String
        get() = "$parentDirPath/$dirName.btn"
    val oriPicPath: String
        get() = "$dirPath/OriPic.jpg"
    val contentPath: String
        get() = "$dirPath/content.json"

    val pdfPath: String
        get() = "$dirPath/$dirName.pdf"

    val zipPath: String
        get() = "$dirPath.zip"

    val oriPic: Bitmap?
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

    var content: ContentClass

    data class ContentClass
    (
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

    enum class Share(val value: Int)
    {
        PDF(1), ZIP(2)
    }

    enum class Location(val value: Int)
    {
        LOCAL(1), CLOUD(2)
    }

    fun makeLocalDir(name: String?)
    {
        if (name == null)
        {
            val c: Calendar = Calendar.getInstance()
            val d = SimpleDateFormat("yyMMdd-hhmmss", Locale.KOREA)
            dirName = d.format(c.time)
            val dirPath = "$parentDirPath/$dirName.btn"
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
            var dir = File("$parentDirPath/$dirName.btn")
            if (!dir.exists())
            {
                dir.mkdir()
                return
            }
            var num = 1
            while (true)
            {
                dirName = name + num.toString()
                dir = File("$parentDirPath/$dirName.btn")
                if (!dir.exists())
                {
                    dir.mkdir()
                    return
                }
                num++
            }
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
        content = ContentClass(list)
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

    fun share(share: Share): Intent
    {
        when (share)
        {
            Share.PDF ->
            {
                val file = exportPdf()
                val uri = if (Build.VERSION.SDK_INT >= 24)
                {
                    FileProvider.getUriForFile(context,
                            context.applicationContext.packageName + ".fileprovider", file)
                }
                else
                {
                    Uri.fromFile(file)
                }
                val intent = Intent()
                intent.apply {
                    putExtra("EXTRA_SUBJECT", dirName)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/*"
                    action = Intent.ACTION_SEND
                }
                return intent
            }
            Share.ZIP ->
            {
                val file = exportZip()
                val intent = Intent()
                intent.apply {
                    putExtra("EXTRA_SUBJECT", dirName)
                    putExtra(Intent.EXTRA_STREAM, file)
                    type = "application/*"
                    action = Intent.ACTION_SEND
                }
                return intent
            }
        }

    }

    private fun exportPdf(): File
    {
        if (Build.VERSION.SDK_INT < 19)
        {
            throw Exception()
        }
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()

        val page = document.startPage(pageInfo)

        val textPaint = TextPaint()
        textPaint.color = Color.BLACK
        textPaint.textSize = 12f
        textPaint.textAlign = Paint.Align.LEFT

        val textTypeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        textPaint.typeface = textTypeface

        var text = ""
        this.content.blockList.forEach {
            text += it.text
            text += "\n"
        }

        val mTextLayout = StaticLayout(text, textPaint, page.canvas.width,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)

        mTextLayout.draw(page.canvas)
        document.finishPage(page)

        try
        {
            val mFileOutStream = FileOutputStream(File(pdfPath))

            // write the document content
            document.writeTo(mFileOutStream)
            mFileOutStream.flush()
            mFileOutStream.close()

        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
        }
        document.close()
        return File(pdfPath)
    }

    private fun exportZip(): File
    {
        val sourceFile = File(dirPath)
        try
        {
            val fos = FileOutputStream(zipPath)
            val bos = BufferedOutputStream(fos)
            val zos = ZipOutputStream(bos)
            zos.setLevel(8)

        }
        catch (e: Exception)
        {

        }
        return File(zipPath)
    }
}