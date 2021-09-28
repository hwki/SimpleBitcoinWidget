package com.brentpanther.bitcoinwidget.strategy

import android.graphics.RectF
import android.text.StaticLayout
import android.view.View.MeasureSpec
import android.widget.TextView
import kotlin.math.roundToInt

// copied from androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper
object TextViewAutoSizeHelper {

    fun findLargestTextSizeWhichFits(textView: TextView, availableSpace: RectF): Int {
        val sizes = IntArray(400) { (it + 8) }
        var bestSizeIndex = 0
        var lowIndex = bestSizeIndex + 1
        var highIndex = sizes.size - 1
        var sizeToTryIndex: Int
        while (lowIndex <= highIndex) {
            sizeToTryIndex = (lowIndex + highIndex) / 2
            if (suggestedSizeFitsInSpace(textView, textView.text.toString(), sizes[sizeToTryIndex], availableSpace)) {
                bestSizeIndex = lowIndex
                lowIndex = sizeToTryIndex + 1
            } else {
                highIndex = sizeToTryIndex - 1
                bestSizeIndex = highIndex
            }
        }
        return sizes[bestSizeIndex]
    }

    private fun suggestedSizeFitsInSpace(textView: TextView, text: String, suggestedSizeInPx: Int, availableSpace: RectF): Boolean {
        textView.paint.textSize = suggestedSizeInPx.toFloat()
        val layoutBuilder = StaticLayout.Builder.obtain(
            text, 0, text.length, textView.paint, availableSpace.right.roundToInt()
        )
        layoutBuilder.setMaxLines(1)
        layoutBuilder.setIncludePad(textView.includeFontPadding)
        val layout = layoutBuilder.build()

        // Lines overflow.
        if (layout.lineCount > 1 || layout.getLineEnd(layout.lineCount - 1) != text.length) {
            return false
        }

        // Height overflow.
        return layout.height <= availableSpace.bottom
    }

    fun findSmallestWidthWhichFits(view: TextView, textSize: Int): Int {
        view.paint.textSize = textSize.toFloat()
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        return view.measuredWidth
    }
}