package com.yezhennan.exp04.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.yezhennan.exp04.model.Card
import androidx.core.graphics.toColorInt

class CardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    init {
        textSize = 20f
        setBackgroundColor(Color.WHITE)
    }

    fun render(card: Card) {
        when {
            card.isMatched -> {
                text = card.toString()
                isEnabled = false
                setBackgroundColor(Color.LTGRAY)
                setTextColor(Color.GRAY)
            }
            card.isChosen -> {
                text = card.toString()
                setBackgroundColor("#BB66BB".toColorInt())
                setTextColor(Color.RED)
            }
            else -> {
                text = "?"
                setBackgroundColor("#4CAF50".toColorInt())
                setTextColor(Color.WHITE)
            }
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        setMeasuredDimension(measuredWidth, (measuredWidth * 1.618).toInt())
    }
}