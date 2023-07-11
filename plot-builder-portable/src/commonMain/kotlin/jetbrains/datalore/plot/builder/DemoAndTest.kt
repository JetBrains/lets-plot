/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.frame.SquareFrameOfReference

object DemoAndTest {
    fun buildGeom(
        layer: GeomLayer,
        xyAesBounds: DoubleRectangle,
        coord: CoordinateSystem,
        flippedAxis: Boolean,
        targetCollector: GeomTargetCollector
    ): SvgComponent {
        return SquareFrameOfReference.buildGeom(
            layer = layer,
            xyAesBounds = xyAesBounds,
            coord = coord,
            flippedAxis = flippedAxis,
            targetCollector = targetCollector
        )
    }
}