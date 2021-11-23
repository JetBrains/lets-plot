/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.tool

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.vis.svg.SvgNode

interface InteractionContext {
    val decorationsLayer: SvgNode
    val eventsManager: EventsManager

    fun findTarget(plotCoord: DoubleVector): InteractionTarget?
}