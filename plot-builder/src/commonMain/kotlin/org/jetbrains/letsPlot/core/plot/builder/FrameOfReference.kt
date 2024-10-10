/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector

abstract class FrameOfReference {
    abstract val transientState: ComponentTransientState

    val bottomGroup = GroupComponent()
    val topGroup = GroupComponent()

    /**
     * Repaints axis and grid but not geoms.
     */
    fun repaintFrame() {
        bottomGroup.clear()
        drawBeforeGeomLayer(bottomGroup)
        topGroup.clear()
        drawAfterGeomLayer(topGroup)
    }

    protected abstract fun drawBeforeGeomLayer(parent: SvgComponent)

    protected abstract fun drawAfterGeomLayer(parent: SvgComponent)

    abstract fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent

    abstract fun setClip(element: SvgComponent)

    /**
     * Throws UnsupportedInteractionException if not supported
     */
    abstract fun checkMouseInteractionSupported(eventSpec: MouseEventSpec)
}