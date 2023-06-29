/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

interface InteractionContext {
    val decorationsLayer: SvgNode
    val eventsManager: EventsManager

    fun findTarget(plotCoord: DoubleVector): InteractionTarget?
}