/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.LineBoxMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.MeasuredText
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText

class PlotLabelSpec(
    override val font: Font,
    override val markdown: Boolean = false
) : LabelSpec {

    override val plainTextLineBoxHeight: Double get() = LineBoxMetrics.plainText(font).height

    override fun layout(labelText: String, lineSpacing: Double): MeasuredText =
        RichText.measure(labelText, font, markdown = markdown, lineInterval = lineSpacing)

    companion object {
        val DUMMY: LabelSpec = object : LabelSpec {
            override val font: Font
                get() = UNSUPPORTED("Dummy Label Spec")

            override val markdown: Boolean
                get() = UNSUPPORTED("Dummy Label Spec")

            override val plainTextLineBoxHeight: Double
                get() = UNSUPPORTED("Dummy Label Spec")

            override fun layout(labelText: String, lineSpacing: Double): MeasuredText =
                UNSUPPORTED("Dummy Label Spec")
        }
    }
}
