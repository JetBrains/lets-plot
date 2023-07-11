/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.interact.ui.EventsManager

interface InteractionTarget {
    fun zoom(geomBounds: DoubleRectangle)

    val geomBounds: DoubleRectangle
}