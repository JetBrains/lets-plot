/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Font

interface LabelSpec {
    val font: Font
    val markdown: Boolean

    fun dimensions(labelText: String): DoubleVector

    fun width(labelText: String): Double

    fun height(): Double
}
