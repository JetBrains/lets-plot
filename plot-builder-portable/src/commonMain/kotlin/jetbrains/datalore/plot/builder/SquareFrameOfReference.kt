/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.FeatureSwitch.MARGINAL_LAYERS
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.assemble.GeomContextBuilder
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.PanelGridTheme
import jetbrains.datalore.plot.builder.theme.PanelTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgRectElement

internal class SquareFrameOfReference(
    private val hScale: Scale<Double>,
    private val vScale: Scale<Double>,
    private val hScaleMapper: ScaleMapper<Double>,
    private val vScaleMapper: ScaleMapper<Double>,
    private val coord: CoordinateSystem,
    private val layoutInfo: TileLayoutInfo,
    private val theme: Theme,
    private val flipAxis: Boolean,
) : TileFrameOfReference {

    var isDebugDrawing: Boolean = false

    private val geomMapperX: ScaleMapper<Double>
    private val geomMapperY: ScaleMapper<Double>
    private val geomCoord: CoordinateSystem

    init {
        if (flipAxis) {
            // flip mappers to 'fool' geom.
            geomMapperX = vScaleMapper
            geomMapperY = hScaleMapper
            geomCoord = coord.flip()
        } else {
            geomMapperX = hScaleMapper
            geomMapperY = vScaleMapper
            geomCoord = coord
        }
    }

    // Rendering

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = true)
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = false)
    }

    private fun drawPanelAndAxis(parent: SvgComponent, beforeGeomLayer: Boolean) {
        val geomBounds: DoubleRectangle = layoutInfo.geomInnerBounds
        val geomOuterBounds: DoubleRectangle = layoutInfo.geomOuterBounds
        val panelTheme = theme.panel()

        // Flip theme
        val hAxisTheme = theme.horizontalAxis(flipAxis)
        val vAxisTheme = theme.verticalAxis(flipAxis)

        val hGridTheme = panelTheme.gridX(flipAxis)
        val vGridTheme = panelTheme.gridY(flipAxis)

        val drawPanel = panelTheme.showRect() && beforeGeomLayer
        val drawGridlines = beforeGeomLayer
        val drawHAxis = when {
            beforeGeomLayer -> !hAxisTheme.isOntop()
            else -> hAxisTheme.isOntop()
        }
        val drawVAxis = when {
            beforeGeomLayer -> !vAxisTheme.isOntop()
            else -> vAxisTheme.isOntop()
        }

        if (drawPanel) {
            val panel = buildPanelComponent(geomBounds, panelTheme)
            parent.add(panel)
        }

        if (drawHAxis || drawGridlines) {
            // X-axis
            val axisInfo = layoutInfo.hAxisInfo!!
            val hAxis = buildAxis(
                hScale,
                hScaleMapper,
                axisInfo,
                hideAxis = !drawHAxis,
                hideAxisBreaks = !layoutInfo.hAxisShown,
                hideGridlines = !drawGridlines,
                coord,
                hAxisTheme,
                hGridTheme,
                gridLineLength = geomBounds.height,
                gridLineDistance = gridLineDistance(geomBounds, geomOuterBounds, axisInfo.orientation),
                isDebugDrawing
            )

            val offset = when {
                MARGINAL_LAYERS -> FeatureSwitch.toAxisOrigin(geomBounds, Orientation.BOTTOM)
                else -> DoubleVector(geomBounds.left, geomBounds.bottom)
            }
            hAxis.moveTo(offset)
            parent.add(hAxis)
        }


        if (drawVAxis || drawGridlines) {
            // Y-axis
            val axisInfo = layoutInfo.vAxisInfo!!
            val vAxis = buildAxis(
                vScale,
                vScaleMapper,
                axisInfo,
                hideAxis = !drawVAxis,
                hideAxisBreaks = !layoutInfo.vAxisShown,
                hideGridlines = !drawGridlines,
                coord,
                vAxisTheme,
                vGridTheme,
                gridLineLength = geomBounds.width,
                gridLineDistance = gridLineDistance(geomBounds, geomOuterBounds, axisInfo.orientation),
                isDebugDrawing
            )

            val offset = when {
                MARGINAL_LAYERS -> FeatureSwitch.toAxisOrigin(geomBounds, Orientation.LEFT)
                else -> geomBounds.origin
            }
            vAxis.moveTo(offset)
            parent.add(vAxis)
        }

        if (isDebugDrawing && !beforeGeomLayer) {
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

//        run {
//            val clipBounds = layoutInfo.clipBounds
//            val rect = SvgRectElement(clipBounds)
//            rect.fillColor().set(Color.DARK_GREEN)
//            rect.strokeWidth().set(0.0)
//            rect.fillOpacity().set(0.3)
//            parent.add(rect)
//        }

        run {
            val rect = SvgRectElement(geomBounds)
            rect.fillColor().set(Color.PINK)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.5)
            parent.add(rect)
        }
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        val hAxisMapper = hScaleMapper
        val vAxisMapper = vScaleMapper

        val hAxisDomain = layoutInfo.hAxisInfo!!.axisDomain
        val vAxisDomain = layoutInfo.vAxisInfo!!.axisDomain
        val aesBounds = DoubleRectangle(
            xRange = DoubleSpan(
                hAxisMapper(hAxisDomain.lowerEnd) as Double,
                hAxisMapper(hAxisDomain.upperEnd) as Double
            ),
            yRange = DoubleSpan(
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


    companion object {
        private fun buildAxis(
            scale: Scale<Double>,
            scaleMapper: ScaleMapper<Double>,
            info: AxisLayoutInfo,
            hideAxis: Boolean,
            hideAxisBreaks: Boolean,
            hideGridlines: Boolean,
            coord: CoordinateSystem,
            axisTheme: AxisTheme,
            gridTheme: PanelGridTheme,
            gridLineLength: Double,
            gridLineDistance: Double,
            isDebugDrawing: Boolean
        ): AxisComponent {
            check(!(hideAxis && hideGridlines)) { "Trying to build an empty axis componenmt" }
            val orientation = info.orientation
            val labelAdjustments = AxisComponent.TickLabelAdjustments(
                orientation = orientation,
                horizontalAnchor = info.tickLabelHorizontalAnchor,
                verticalAnchor = info.tickLabelVerticalAnchor,
                rotationDegree = info.tickLabelRotationAngle,
                additionalOffsets = info.tickLabelAdditionalOffsets
            )

            val breaksData = AxisUtil.breaksData(
                scale.getScaleBreaks(),
                scaleMapper,
                coord,
                orientation.isHorizontal
            )

            val axis = AxisComponent(
                length = info.axisLength,
                orientation = orientation,
                breaksData = breaksData,
                labelAdjustments = labelAdjustments,
                gridLineLength = gridLineLength,
                gridLineDistance = gridLineDistance,
                axisTheme = axisTheme,
                gridTheme = gridTheme,
                hideAxis = hideAxis,
                hideAxisBreaks = hideAxisBreaks,
                hideGridlines = hideGridlines
            )

            if (isDebugDrawing) {
                val rect = SvgRectElement(info.tickLabelsBounds)
                rect.strokeColor().set(Color.GREEN)
                rect.strokeWidth().set(1.0)
                rect.fillOpacity().set(0.0)
                axis.add(rect)
            }
            return axis
        }

        private fun buildPanelComponent(bounds: DoubleRectangle, theme: PanelTheme): SvgRectElement {
            return SvgRectElement(bounds).apply {
                strokeColor().set(theme.rectColor())
                strokeWidth().set(theme.rectStrokeWidth())
                fillColor().set(theme.rectFill())
            }
        }

        /**
         * 'internal' access for tests.
         */
        internal fun buildGeom(
            layer: GeomLayer,
            xAesMapper: ScaleMapper<Double>,
            yAesMapper: ScaleMapper<Double>,
            xyAesBounds: DoubleRectangle,
            coord: CoordinateSystem,
            flippedAxis: Boolean,
            targetCollector: GeomTargetCollector
        ): SvgComponent {
            val rendererData = LayerRendererUtil.createLayerRendererData(
                layer,
                xAesMapper, yAesMapper
            )

            @Suppress("NAME_SHADOWING")
            // val flippedAxis = layer.isYOrientation xor flippedAxis
            // (XOR issue: https://youtrack.jetbrains.com/issue/KT-52296/Kotlin-JS-the-xor-operation-sometimes-evaluates-to-int-value-ins)
            val flippedAxis = if (layer.isYOrientation) !flippedAxis else flippedAxis

            val aestheticMappers = rendererData.aestheticMappers
            val aesthetics = rendererData.aesthetics

            @Suppress("NAME_SHADOWING")
            val coord = when (layer.isYOrientation) {
                true -> coord.flip()
                false -> coord
            }

            @Suppress("NAME_SHADOWING")
            val targetCollector = targetCollector.let {
                when {
                    flippedAxis -> it.withFlippedAxis()
                    else -> it
                }
            }.let {
                when {
                    layer.isYOrientation -> it.withYOrientation()
                    else -> it
                }
            }

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

        private fun gridLineDistance(
            geomInnerBounds: DoubleRectangle,
            geomOuterBounds: DoubleRectangle,
            orientation: Orientation
        ): Double {
            return when (orientation) {
                Orientation.LEFT -> geomInnerBounds.left - geomOuterBounds.left
                Orientation.RIGHT -> geomOuterBounds.right - geomInnerBounds.right
                Orientation.TOP -> geomInnerBounds.top - geomOuterBounds.top
                Orientation.BOTTOM -> geomOuterBounds.bottom - geomInnerBounds.bottom
            }
        }
    }
}