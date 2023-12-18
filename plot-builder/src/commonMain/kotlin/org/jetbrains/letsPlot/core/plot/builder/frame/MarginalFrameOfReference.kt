/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReference
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal class MarginalFrameOfReference(
    private val geomBounds: DoubleRectangle,
    private val adjustedDomain: DoubleRectangle,
    private val coord: CoordinateSystem,
    private val plotBackground: Color,
    private val penColor: Color,
    private val isDebugDrawing: Boolean,
    private val superscriptExponent: Boolean,
) : FrameOfReference {
    override fun drawBeforeGeomLayer(parent: SvgComponent) {}

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        if (isDebugDrawing) {
            parent.add(SvgRectElement(geomBounds).apply {
                strokeColor().set(Color.ORANGE)
                fillColor().set(Color.ORANGE)
                strokeWidth().set(0.0)
                fillOpacity().set(0.5)
            })
        }
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        val layerComponent = SquareFrameOfReference.buildGeom(
            layer,
            xyAesBounds = adjustedDomain,
            coord,
            flippedAxis = false,
            targetCollector,
            plotBackground,
            penColor,
            superscriptExponent
        )

        layerComponent.moveTo(geomBounds.origin)
        layerComponent.clipBounds(DoubleRectangle(DoubleVector.ZERO, geomBounds.dimension))
        return layerComponent
    }
}