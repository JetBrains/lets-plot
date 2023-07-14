/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector

interface FrameOfReference {
    fun drawBeforeGeomLayer(parent: SvgComponent)

    fun drawAfterGeomLayer(parent: SvgComponent)

    fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent
}