/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.MeasuredText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextLayout

interface LabelSpec {
    val font: Font
    val markdown: Boolean

    val defaultLineHeight: Double

    fun measure(labelText: String, lineInterval: Double = 0.0, trimLines: Boolean = false): MeasuredText

    fun measureLayout(labelText: String, lineInterval: Double = 0.0, trimLines: Boolean = false): TextLayout =
        measure(labelText, lineInterval, trimLines).layout
}