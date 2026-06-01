/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.MeasuredText

interface LabelSpec {
    val font: Font
    val markdown: Boolean

    val plainTextLineBoxHeight: Double

    fun layout(labelText: String, lineSpacing: Double = 0.0): MeasuredText
}
