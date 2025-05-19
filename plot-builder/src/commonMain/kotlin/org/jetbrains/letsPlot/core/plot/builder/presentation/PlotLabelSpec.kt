/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
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
        return RichText.estimateWidth(labelText, font, markdown = markdown, widthEstimator = TextWidthEstimator::widthCalculator)
    }

    override fun height(): Double {
        return font.size.toDouble()
    }

    override fun multilineHeight(labelText: String): Double {
        labelText.count { it == '\n' }.let { newLinesCount ->
            return height() + font.size * newLinesCount
        }
    }

    companion object {
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
