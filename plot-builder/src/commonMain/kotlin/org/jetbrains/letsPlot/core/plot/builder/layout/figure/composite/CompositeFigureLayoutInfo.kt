/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendsBlockInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.FigureLayoutInfo

class CompositeFigureLayoutInfo constructor(
    figureSize: DoubleVector,

    /**
     * Figure area without border thickness and margins.
     * Relative to the entire figure origin
     */
    val contentAreaBounds: DoubleRectangle,

    /**
     * Figure "content area" area without titles and legends.
     * Relative to the entire figure origin
     */
    val elementsAreaBounds: DoubleRectangle,

    val legendsBlockInfos: List<LegendsBlockInfo>,
) : FigureLayoutInfo(figureSize)