package com.unitech.boardtonote.data

import android.graphics.Rect
import com.fasterxml.jackson.annotation.JsonIgnore

interface BTNInterface
{

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
}