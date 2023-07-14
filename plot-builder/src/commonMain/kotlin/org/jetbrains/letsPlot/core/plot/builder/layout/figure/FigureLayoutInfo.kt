/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

open class FigureLayoutInfo(
    val figureSize: DoubleVector,
    /**
     * Plot withot axis and facet labels.
     * Relative to the entire figure origin
     */
    val geomAreaBounds: DoubleRectangle
) {

}