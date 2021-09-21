/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.assemble.GeomContextBuilder
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgRectElement

internal class SquareFrameOfReference(
    private val hScale: Scale<Double>,
    private val vScale: Scale<Double>,
    private val coord: CoordinateSystem,
    private val layoutInfo: TileLayoutInfo,
    private val theme: Theme,
    private val flipAxis: Boolean,
) : TileFrameOfReference {

    var isDebugDrawing: Boolean = false

    private val geomMapperX: (Double?) -> Double?
    private val geomMapperY: (Double?) -> Double?
    private val geomCoord: CoordinateSystem

    init {
        if (flipAxis) {
            // flip mappers to 'fool' geom.
            geomMapperX = vScale.mapper
            geomMapperY = hScale.mapper
            geomCoord = coord.flip()
        } else {
            geomMapperX = hScale.mapper
            geomMapperY = vScale.mapper
            geomCoord = coord
        }
    }

    // Rendering

    override fun drawAxis(parent: SvgComponent) {
        val geomBounds: DoubleRectangle = layoutInfo.geomBounds

        // Flip theme
        val (xAxisTheme, yAxisTheme) = when {
            flipAxis -> Pair(theme.axisY(), theme.axisX())
            else -> Pair(theme.axisX(), theme.axisY())
        }

        // X-axis (below geom area)
        if (layoutInfo.xAxisShown) {
            val axis = buildAxis(hScale, layoutInfo.xAxisInfo!!, coord, xAxisTheme, isDebugDrawing)
            axis.moveTo(DoubleVector(geomBounds.left, geomBounds.bottom))
            parent.add(axis)
        }
        // Y-axis (to the left from geom area, axis elements have negative x-positions)
        if (layoutInfo.yAxisShown) {
            val axis = buildAxis(vScale, layoutInfo.yAxisInfo!!, coord, yAxisTheme, isDebugDrawing)
            axis.moveTo(geomBounds.origin)
            parent.add(axis)
        }

        if (isDebugDrawing) {
            drawDebugShapes(parent, geomBounds)
        }
    }

    private fun drawDebugShapes(parent: SvgComponent, geomBounds: DoubleRectangle) {
        run {
            val tileBounds = layoutInfo.bounds
            val rect = SvgRectElement(tileBounds)
            rect.fillColor().set(Color.BLACK)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.1)
            parent.add(rect)
        }

        run {
            val clipBounds = layoutInfo.clipBounds
            val rect = SvgRectElement(clipBounds)
            rect.fillColor().set(Color.DARK_GREEN)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.3)
            parent.add(rect)
        }

        run {
            val rect = SvgRectElement(geomBounds)
            rect.fillColor().set(Color.PINK)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.5)
            parent.add(rect)
        }
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        val hAxisMapper = hScale.mapper
        val vAxisMapper = vScale.mapper

        val hAxisDomain = layoutInfo.xAxisInfo!!.axisDomain!!
        val vAxisDomain = layoutInfo.yAxisInfo!!.axisDomain!!
        val aesBounds = DoubleRectangle(
            xRange = ClosedRange(
                hAxisMapper(hAxisDomain.lowerEnd) as Double,
                hAxisMapper(hAxisDomain.upperEnd) as Double
            ),
            yRange = ClosedRange(
                vAxisMapper(vAxisDomain.lowerEnd) as Double,
                vAxisMapper(vAxisDomain.upperEnd) as Double
            )
        )

        return buildGeom(
            layer,
            geomMapperX, geomMapperY,
            xyAesBounds = aesBounds,
            geomCoord,
            flipAxis,
            targetCollector
        )
    }

    override fun applyClientLimits(clientBounds: DoubleRectangle): DoubleRectangle {
        return geomCoord.applyClientLimits(clientBounds)
    }


    companion object {
        private fun buildAxis(
            scale: Scale<Double>,
            info: AxisLayoutInfo,
            coord: CoordinateSystem,
            theme: AxisTheme,
            isDebugDrawing: Boolean
        ): AxisComponent {
            val axis = AxisComponent(info.axisLength, info.orientation!!)
            AxisUtil.setBreaks(axis, scale, coord, info.orientation.isHorizontal)
            AxisUtil.applyLayoutInfo(axis, info)
            AxisUtil.applyTheme(axis, theme)
            if (isDebugDrawing) {
                if (info.tickLabelsBounds != null) {
                    val rect = SvgRectElement(info.tickLabelsBounds)
                    rect.strokeColor().set(Color.GREEN)
                    rect.strokeWidth().set(1.0)
                    rect.fillOpacity().set(0.0)
                    axis.add(rect)
                }
            }
            return axis
        }

        private fun buildGeom(
            layer: GeomLayer,
            xAesMapper: (Double?) -> Double?,
            yAesMapper: (Double?) -> Double?,
            xyAesBounds: DoubleRectangle,
            coord: CoordinateSystem,
            flippedAxis: Boolean,
            targetCollector: GeomTargetCollector
        ): SvgComponent {
            val rendererData = LayerRendererUtil.createLayerRendererData(
                layer,
                xAesMapper, yAesMapper
            )

            val aestheticMappers = rendererData.aestheticMappers
            val aesthetics = rendererData.aesthetics

            val ctx = GeomContextBuilder()
                .flipped(flippedAxis)
                .aesthetics(aesthetics)
                .aestheticMappers(aestheticMappers)
                .aesBounds(xyAesBounds)
                .geomTargetCollector(targetCollector)
                .build()

            val pos = rendererData.pos
            val geom = layer.geom

            return SvgLayerRenderer(aesthetics, geom, pos, coord, ctx)
        }
    }
}