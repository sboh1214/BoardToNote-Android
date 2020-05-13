package com.unitech.boardtonote.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.unitech.boardtonote.R
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.data.BtnInterface
import com.unitech.boardtonote.databinding.BottomBlockBinding
import com.unitech.boardtonote.helper.SnackBarInterface
import java.util.*

class BottomBlockFragment(private val block: BtnInterface.BlockClass)
    : BottomSheetDialogFragment()
{
    private lateinit var snackBarInterface: SnackBarInterface
    private lateinit var tts: TextToSpeech
    private lateinit var eA: EditActivity
    private lateinit var b: BottomBlockBinding

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        snackBarInterface = activity as SnackBarInterface
        eA = activity as EditActivity
        tts = TextToSpeech(activity!!.applicationContext, TextToSpeech.OnInitListener {
            fun onInit(status: Int)
            {
                if (status != TextToSpeech.ERROR)
                {
                    tts.language = Locale.ENGLISH
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        b = BottomBlockBinding.inflate(inflater, container, false)

        b.ButtonCopy.setOnClickListener {
            val manager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Board To Note", block.text)
            manager.setPrimaryClip(clip)
            snackBarInterface.snackBar("Copied to Clipboard")
            dismiss()
        }
        b.ButtonTTS.setOnClickListener {
            ttsLollipop(block.text)
            dismiss()
        }
        b.ButtonDelete.setOnClickListener {
            eA.btnClass.content!!.blockList.remove(block)
            eA.btnClass.saveContent()
            eA.blockAdapter.notifyDataSetChanged()
            snackBarInterface.snackBar("Deleted Block")
            dismiss()
        }
        b.ButtonShare.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, eA.btnClass.dirName)
                putExtra(Intent.EXTRA_TEXT, block.text)
            }
            eA.startActivity(Intent.createChooser(intent, "Share Block"))
            dismiss()
        }
        b.ButtonEdit.setOnClickListener {
            dismiss()
            eA.supportFragmentManager
                    .beginTransaction()
                    .replace(com.unitech.boardtonote.R.id.Frame_Edit, BlockFragment(block))
                    .addToBackStack(null)
                    .commit()
        }
        b.ButtonInfo.setOnClickListener {
            dismiss()
            AlertDialog.Builder(eA).apply {
                setTitle("More Info")
                setMessage("Text : ${block.text} \nFont Size : ${block.fontSize} \nConfidence : ${block.confidence}")
                setPositiveButton("Dismiss") { dialogInterface, _ -> dialogInterface.dismiss() }
                show()
            }
        }

        return b.root
    }

    private fun ttsLollipop(text: String)
    {
        val utteranceId = this.hashCode().toString() + ""
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
}