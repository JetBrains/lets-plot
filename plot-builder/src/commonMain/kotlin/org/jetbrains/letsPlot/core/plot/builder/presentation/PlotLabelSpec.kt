/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.MeasuredText
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText

class PlotLabelSpec(
    override val font: Font,
    override val markdown: Boolean = false
) : LabelSpec {

    override val defaultLineHeight: Double get() = LineLayoutMetrics.plainText(font).height

    override fun measure(labelText: String, lineInterval: Double, trimLines: Boolean): MeasuredText =
        RichText.measure(
            labelText, font,
            markdown = markdown,
            lineInterval = lineInterval,
            trimLines = trimLines,
        )

    companion object {
        val DUMMY: LabelSpec = object : LabelSpec {
            override val font: Font
                get() = UNSUPPORTED("Dummy Label Spec")

            override val markdown: Boolean
                get() = UNSUPPORTED("Dummy Label Spec")

            override val defaultLineHeight: Double
                get() = UNSUPPORTED("Dummy Label Spec")

            override fun measure(labelText: String, lineInterval: Double, trimLines: Boolean): MeasuredText =
                UNSUPPORTED("Dummy Label Spec")
        }
    }
}
