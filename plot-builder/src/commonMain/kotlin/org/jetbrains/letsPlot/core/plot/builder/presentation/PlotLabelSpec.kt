/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.LineDimensions
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText

class PlotLabelSpec(
    override val font: Font,
    override val markdown: Boolean = false
) : LabelSpec {

    override fun lineDimensions(labelText: String): List<LineDimensions> {
        return RichText.estimateLineDimensions(labelText, font, markdown = markdown)
    }

    override fun defaultLine(): LineDimensions {
        return LineDimensions(0.0, LineLayoutMetrics.plainText(font))
    }

    companion object {
        val DUMMY: LabelSpec = object : LabelSpec {
            override val font: Font
                get() = UNSUPPORTED("Dummy Label Spec")

            override val markdown: Boolean
                get() = UNSUPPORTED("Dummy Label Spec")

            override fun lineDimensions(labelText: String): List<LineDimensions> {
                UNSUPPORTED("Dummy Label Spec")
            }

            override fun defaultLine(): LineDimensions {
                UNSUPPORTED("Dummy Label Spec")
            }
        }
    }
}
