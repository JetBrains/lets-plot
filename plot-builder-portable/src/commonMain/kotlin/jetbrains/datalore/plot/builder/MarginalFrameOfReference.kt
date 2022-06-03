/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.vis.svg.SvgRectElement

internal class MarginalFrameOfReference(
    private val bounds: DoubleRectangle,
    private val hScaleMapper: ScaleMapper<Double>,
    private val vScaleMapper: ScaleMapper<Double>,
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
        val aesBounds = DoubleRectangle(
            xRange = DoubleSpan(
                hScaleMapper(bounds.left) as Double,
                hScaleMapper(bounds.right) as Double
            ),
            yRange = DoubleSpan(
                vScaleMapper(bounds.top) as Double,
                vScaleMapper(bounds.bottom) as Double
            )
        )

        val layerComponent = SquareFrameOfReference.buildGeom(
            layer,
            hScaleMapper, vScaleMapper,
            xyAesBounds = aesBounds,
            coord,
            flippedAxis = false,
            targetCollector
        )

        layerComponent.moveTo(bounds.origin)
        layerComponent.clipBounds(DoubleRectangle(DoubleVector.ZERO, bounds.dimension))
        return layerComponent


//        return object : SvgComponent() {
//            override fun buildComponent() {
//                rootGroup.children().add(
//                    SvgRectElement(bounds).apply {
//                        strokeColor().set(Color.ORANGE)
//                        fillColor().set(Color.ORANGE)
//                        strokeWidth().set(0.0)
//                        fillOpacity().set(0.3)
//                    }
//                )
//            }
//        }

    }
}