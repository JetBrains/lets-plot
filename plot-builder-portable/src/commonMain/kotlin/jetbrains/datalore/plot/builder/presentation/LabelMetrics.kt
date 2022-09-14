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
    override val widthScaleFactor: Double

    /**
     * for Serializable
     */
    constructor() {
        this.font = Font(FontFamily.DEFAULT_FONT_FAMILY, 0)
        isMonospaced = false
        widthScaleFactor = 1.0
    }

    @JvmOverloads
    constructor(font: Font, monospaced: Boolean = false, widthScaleFactor: Double = 1.0) {
        this.font = font
        isMonospaced = monospaced
        this.widthScaleFactor = widthScaleFactor
    }

    override fun dimensions(labelText: String): DoubleVector {
        return DoubleVector(width(labelText), height())
    }

    override fun width(labelText: String): Double {
        return applyWidthAdjustment(
            if (isMonospaced)
                monospacedWidth(labelText.length)
            else
                FONT_WIDTH_SCALE_FACTOR * TextWidthEstimator.textWidth(labelText, font)
        )
    }

    private fun applyWidthAdjustment(width: Double): Double {
        return widthScaleFactor * width
    }

    private fun monospacedWidth(labelLength: Int): Double {
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
        private const val FONT_WIDTH_SCALE_FACTOR = 0.85026 // See explanation here: font_width_scale_factor.md
    }
}
