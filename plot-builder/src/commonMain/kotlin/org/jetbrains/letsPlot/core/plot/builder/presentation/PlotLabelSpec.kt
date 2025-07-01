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

    override fun dimensions(labelText: String): List<DoubleVector> {
        val maxWidth = maxWidth(labelText)
        return heights(labelText).map { height ->
            DoubleVector(maxWidth, height)
        }
    }

    override fun totalDimensions(labelText: String): DoubleVector {
        val totalHeight = heights(labelText).sum()
        return DoubleVector(maxWidth(labelText), totalHeight)
    }

    override fun maxWidth(labelText: String): Double {
        return RichText.estimateWidth(labelText, font, markdown = markdown, widthCalculator = TextWidthEstimator::widthCalculator)
    }

    // TODO: Use RichText.estimateHeights()
    override fun heights(labelText: String): List<Double> {
        val fontSize = font.size.toDouble()
        return labelText.split('\n').map { fontSize }
    }

    override fun regularLineHeight(): Double {
        return heights("").single()
    }

    companion object {
        val DUMMY: LabelSpec = object : LabelSpec {
            override val font: Font
                get() = UNSUPPORTED("Dummy Label Spec")

            override val markdown: Boolean
                get() = UNSUPPORTED("Dummy Label Spec")

            override fun dimensions(labelText: String): List<DoubleVector> {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun totalDimensions(labelText: String): DoubleVector {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun maxWidth(labelText: String): Double {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun heights(labelText: String): List<Double> {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun regularLineHeight(): Double {
                UNSUPPORTED("Dummy Label Spec")
            }
        }
    }
}
