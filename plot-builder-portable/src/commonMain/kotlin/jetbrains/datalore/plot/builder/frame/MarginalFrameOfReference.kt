/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.frame

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.coord.CoordinatesMapper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.FrameOfReference
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.vis.svg.SvgRectElement

internal class MarginalFrameOfReference(
    private val bounds: DoubleRectangle,
    private val coordMapper: CoordinatesMapper,
    private val coord: CoordinateSystem,
    private val isDebugDrawing: Boolean,
) : FrameOfReference {
    override fun drawBeforeGeomLayer(parent: SvgComponent) {}

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        if (isDebugDrawing) {
            parent.add(SvgRectElement(bounds).apply {
                strokeColor().set(Color.ORANGE)
                fillColor().set(Color.ORANGE)
                strokeWidth().set(0.0)
                fillOpacity().set(0.5)
            })
        }
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        val leftTop = coordMapper.toClient(bounds.origin)!!
        val rightBottom = coordMapper.toClient(bounds.origin.add(bounds.dimension))!!
        val aesBounds = DoubleRectangle(
            xRange = DoubleSpan(leftTop.x, rightBottom.x),
            yRange = DoubleSpan(leftTop.y, rightBottom.y)
        )

        val layerComponent = SquareFrameOfReference.buildGeom(
            layer,
            xyAesBounds = aesBounds,
            coord,
            flippedAxis = false,
            targetCollector
        )

        layerComponent.moveTo(bounds.origin)
        layerComponent.clipBounds(DoubleRectangle(DoubleVector.ZERO, bounds.dimension))
        return layerComponent
    }
}