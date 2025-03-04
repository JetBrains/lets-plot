/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText

class PlotLabelSpec(
    override val font: Font,
    override val markdown: Boolean = false
) : LabelSpec {

    override fun dimensions(labelText: String): DoubleVector {
        return DoubleVector(width(labelText), height())
    }

    override fun multilineDimensions(labelText: String): DoubleVector {
        return DoubleVector(width(labelText), multilineHeight(labelText))
    }

    override fun width(labelText: String): Double {
        return RichText.estimateWidth(labelText, font, markdown = markdown, widthEstimator = ::widthCalculator)
    }

    override fun height(): Double {
        return font.size + 2 * LABEL_PADDING
    }

    override fun multilineHeight(labelText: String): Double {
        labelText.count { it == '\n' }.let { newLinesCount ->
            return height() + font.size * newLinesCount
        }
    }

    companion object {
        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO = 0.67 //0.48; // 0.42;
        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED = 0.6
        private const val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075
        private const val LABEL_PADDING = 0.0 //2;
        private const val FONT_WIDTH_SCALE_FACTOR = 0.85026 // See explanation here: font_width_scale_factor.md

        private fun widthCalculator(text: String, font: Font): Double {
            return if (font.isMonospased) {
                // ToDo: should take in account font family adjustment parameters.
                monospacedWidth(text.length, font)
            } else {
                FONT_WIDTH_SCALE_FACTOR * TextWidthEstimator.textWidth(text, font)
            }.let {
                it * font.family.widthFactor
            }
        }

        /**
         * The old way.
         */
        private fun monospacedWidth(labelLength: Int, font: Font): Double {
            val ratio = FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED
            val width = labelLength.toDouble() * font.size * ratio + 2 * LABEL_PADDING
            return if (font.isBold) {
                // ToDo: switch to new ratios.
                width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
            } else {
                width
            }
        }

        val DUMMY: LabelSpec = object : LabelSpec {
            override val font: Font
                get() = UNSUPPORTED("Dummy Label Spec")

            override val markdown: Boolean
                get() = UNSUPPORTED("Dummy Label Spec")

            override fun dimensions(labelText: String): DoubleVector {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun multilineDimensions(labelText: String): DoubleVector {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun width(labelText: String): Double {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun height(): Double {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun multilineHeight(labelText: String): Double {
                UNSUPPORTED("Dummy Label Spec")
            }
        }
    }
}
