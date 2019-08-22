package com.unitech.boardtonote.data

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
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
import com.unitech.boardtonote.Constant
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipOutputStream

interface BTNInterface
{
    val location: Int
    val state: Int?
    val tag: String

    val context: Context
    var dirName: String?

    val parentDirPath: String
    val dirPath: String
    val oriPicPath: String
        get() = "$dirPath/OriPic.jpg"
    val contentPath: String
        get() = "$dirPath/content.json"

    val pdfPath: String
        get() = "$dirPath/$dirName.pdf"

    val zipPath: String
        get() = "$dirPath.zip"

    val oriPic: Bitmap?

    var content: ContentClass

    data class ContentClass
    (
            var blockList: ArrayList<BlockClass>
    )

    data class BlockClass
    (
            var text: String,
            val confidence: Float?,
            val language: List<String?>,
            @JsonIgnore
            val frame: Rect?,
            var fontSize: Float
    )

    var onLocationAndState: (Int, Int?) -> Boolean

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
            val parcelFileDescriptor: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                    ?: return false
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            val stream = FileOutputStream(oriPicPath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            parcelFileDescriptor.close()
            return true
        }
        catch (e: Exception)
        {
            Log.e(tag, e.toString())
            false
        }
    }

    fun getOriPicRatio(): Float?
    {
        if (oriPic == null)
        {
            return null
        }
        val height = oriPic!!.width.toFloat()
        val width = oriPic!!.height.toFloat()
        return (width / height)
    }

    fun decodeOriPic(width: Int, height: Int?): Bitmap?
    {
        return try
        {
            val options = BitmapFactory.Options()
            options.outWidth = width
            if (height != null)
            {
                options.outHeight = height
            }
            BitmapFactory.decodeFile(oriPicPath, options)
        }
        catch (e: Exception)
        {
            null
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

    fun asyncGetContent(onSuccess: () -> Boolean, onFailure: () -> Boolean)
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
            onSuccess()
        }
    }

    fun analyze(onSuccess: () -> Boolean, onFailure: () -> Boolean)
    {
        if (oriPic == null)
        {
            onFailure()
            return
        }
        val trace = FirebasePerformance.getInstance().newTrace("process_image")
        trace.start()
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(oriPic!!)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image).apply {
            addOnSuccessListener { firebaseVisionText ->
                trace.stop()
                saveVisionText(firebaseVisionText)
                Log.i(tag, "analyze() Success $dirName")
                Log.v(tag, firebaseVisionText.text.replace("\n", " "))
                onSuccess()
            }
            addOnFailureListener { e ->
                trace.stop()
                onFailure()
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
            val blockClass = BlockClass(b.text, b.confidence, b.recognizedLanguages.map { lang -> lang.languageCode }, b.boundingBox, 22f)
            list.add(blockClass)
        }
        content = ContentClass(list)
        val mapper = jacksonObjectMapper()
        mapper.writerWithDefaultPrettyPrinter().writeValue(File(contentPath), content)
    }

    fun share(share: Int)
    {
        when (share)
        {
            Constant.sharePdf ->
            {
                exportPdf()
            }
            Constant.shareZip ->
            {
                val file = exportZip()
                val intent = Intent()
                intent.apply {
                    putExtra("EXTRA_SUBJECT", dirName)
                    putExtra(Intent.EXTRA_STREAM, file)
                    type = "application/*"
                    action = Intent.ACTION_SEND
                }
            }
            else              ->
            {
            }
        }
    }

    private fun exportPdf()
    {
        if (Build.VERSION.SDK_INT < 19)
        {
            throw Exception()
        }
        asyncGetContent({
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
            val file = File(pdfPath)
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
            context.startActivity(intent)
            true
        }, { false })
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

    fun saveContent()
    {
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
}