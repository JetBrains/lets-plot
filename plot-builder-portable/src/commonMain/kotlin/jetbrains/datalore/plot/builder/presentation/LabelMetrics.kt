/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Font
import jetbrains.datalore.base.values.FontFamily
import kotlin.jvm.JvmOverloads

//ToDo:
interface Serializable

class LabelMetrics : LabelSpec,
    Serializable {

    override val font: Font
    override val isMonospaced: Boolean

    /**
     * for Serializable
     */
    constructor() {
        this.font = Font(FontFamily.DEFAULT_FONT_FAMILY, 0)
        isMonospaced = false
    }

    @JvmOverloads
    constructor(font: Font, monospaced: Boolean = false) {
        this.font = font
        isMonospaced = monospaced
    }

    override fun dimensions(labelText: String): DoubleVector {
        return DoubleVector(width(labelText), height())
    }

    override fun width(labelText: String): Double {
        return widthByLength(labelText.length)
    }

    override fun widthByLength(labelLength: Int): Double {
        var ratio =
            FONT_SIZE_TO_GLYPH_WIDTH_RATIO
        if (isMonospaced) {
            ratio =
                FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED
        }

        val width = labelLength.toDouble() * font.size * ratio + 2 * LABEL_PADDING
        return if (font.isBold) {
            width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
        } else width
    }

    override fun height(): Double {
        return font.size + 2 * LABEL_PADDING
    }

    companion object {
        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO = 0.67 //0.48; // 0.42;
        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED = 0.6
        private const val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075
        private const val LABEL_PADDING = 0.0 //2;
    }
}
