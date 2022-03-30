/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.CharCategory.Companion.getCharRatio
import kotlin.jvm.JvmOverloads

//ToDo:
interface Serializable

class LabelMetrics : LabelSpec,
    Serializable {

    override val fontSize: Double
    override val isBold: Boolean
    override val isMonospaced: Boolean

    /**
     * for Serializable
     */
    constructor() {
        this.fontSize = 0.0
        isBold = false
        isMonospaced = false
    }

    /**
     * @param fontSize in 'px' (same meaning as in CSS)
     */
    @JvmOverloads
    constructor(fontSize: Double, bold: Boolean = false, monospaced: Boolean = false) {
        this.fontSize = fontSize
        isBold = bold
        isMonospaced = monospaced
    }

    override fun dimensions(labelLength: Int): DoubleVector {
        return DoubleVector(width(labelLength), height())
    }

    override fun width(labelLength: Int): Double {
        var ratio =
            FONT_SIZE_TO_GLYPH_WIDTH_RATIO
        if (isMonospaced) {
            ratio =
                FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED
        }

        val width = labelLength.toDouble() * fontSize * ratio + 2 * LABEL_PADDING
        return if (isBold) {
            width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
        } else width
    }

    override fun width(text: String, fontFamily: String?): Double {
        val options = getOptionsForFont(fontFamily)
        val width = text.map { getCharRatio(it, options) }.sum() * fontSize * options.fontRatio
        return if (isBold) {
            width * options.fontBoldRatio
        } else {
            width
        }
    }

    override fun height(): Double {
        return fontSize + 2 * LABEL_PADDING
    }

    companion object {
        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO = 0.67 //0.48; // 0.42;
        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED = 0.6
        private const val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075
        private const val LABEL_PADDING = 0.0 //2;
    }
}
