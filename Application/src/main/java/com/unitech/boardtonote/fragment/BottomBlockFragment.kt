package com.unitech.boardtonote.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.unitech.boardtonote.activity.EditActivity
import com.unitech.boardtonote.data.BTNInterface
import com.unitech.boardtonote.helper.SnackBarInterface
import kotlinx.android.synthetic.main.bottom_block.view.*
import java.util.*

class BottomBlockFragment(private val block: BTNInterface.BlockClass)
    : BottomSheetDialogFragment()
{
    private lateinit var snackBarInterface: SnackBarInterface
    private lateinit var tts: TextToSpeech
    private lateinit var editActivity: EditActivity

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        snackBarInterface = context as SnackBarInterface
        editActivity = activity as EditActivity
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
        val view = inflater.inflate(com.unitech.boardtonote.R.layout.bottom_block, container, false)
        view.Button_Copy.setOnClickListener {
            val manager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Board To Note", block.text)
            manager.setPrimaryClip(clip)
            snackBarInterface.snackBar("Copied to Clipboard")
            dismiss()
        }
        view.Button_TTS.setOnClickListener {
            if (Build.VERSION.SDK_INT > 20)
            {
                ttsLollipop(block.text)
            }
            else
            {
                ttsKitKat(block.text)
            }
            dismiss()
        }
        view.Button_Delete.setOnClickListener {
            editActivity.btnClass.content.blockList.remove(block)
            editActivity.blockAdapter.notifyDataSetChanged()
            snackBarInterface.snackBar("Deleted Block")
            dismiss()
        }
        view.Button_Share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, editActivity.btnClass.dirName)
                putExtra(Intent.EXTRA_TEXT, block.text)
            }
            editActivity.startActivity(Intent.createChooser(intent, "Share Block"))
            dismiss()
        }
        return view
    }

    @Suppress("DEPRECATION")
    private fun ttsKitKat(text: String)
    {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsLollipop(text: String)
    {
        val utteranceId = this.hashCode().toString() + ""
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
}