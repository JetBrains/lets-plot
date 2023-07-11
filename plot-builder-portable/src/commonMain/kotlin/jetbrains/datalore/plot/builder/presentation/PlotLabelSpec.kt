/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Font

class PlotLabelSpec(
    override val font: Font
) : LabelSpec {

    override fun dimensions(labelText: String): DoubleVector {
        return DoubleVector(width(labelText), height())
    }

    override fun width(labelText: String): Double {
        return if (font.isMonospased) {
            // ToDo: should take in account font family adjustment parameters.
            monospacedWidth(labelText.length)
        } else {
            FONT_WIDTH_SCALE_FACTOR * TextWidthEstimator.textWidth(labelText, font)
        }.let {
            it * font.family.widthFactor
        }
    }

    /**
     * The old way.
     */
    private fun monospacedWidth(labelLength: Int): Double {
        val ratio = FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED
        val width = labelLength.toDouble() * font.size * ratio + 2 * LABEL_PADDING
        return if (font.isBold) {
            // ToDo: switch to new ratios.
            width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
        } else {
            width
        }
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

        val DUMMY: LabelSpec = object : LabelSpec {
            override val font: Font
                get() = UNSUPPORTED("Dummy Label Spec")

            override fun dimensions(labelText: String): DoubleVector {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun width(labelText: String): Double {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun height(): Double {
                UNSUPPORTED("Dummy Label Spec")
            }
        }
    }
}
