/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.geom.LiveMapGeom
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.assemble.GeomContextBuilder
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.interact.loc.LayerTargetCollectorWithLocator
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.FACET_H_PADDING
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.FACET_TAB_HEIGHT
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.FACET_V_PADDING
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.facetColHeadHeight
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.facetColLabelSize
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgRectElement

internal class PlotTile(
    layers: List<GeomLayer>,
    private val myScaleX: Scale<Double>,
    private val myScaleY: Scale<Double>,
    private val myTilesOrigin: DoubleVector,
    private val myLayoutInfo: TileLayoutInfo,
    private val myCoord: CoordinateSystem,
    private val myTheme: Theme
) : SvgComponent() {

    private val myDebugDrawing = ValueProperty(false)
    private val myLayers: List<GeomLayer>
    private val myTargetLocators = ArrayList<GeomTargetLocator>()
    private var myShowAxis: Boolean = false

    var liveMapFigure: SomeFig? = null
        private set

    val targetLocators: List<GeomTargetLocator>
        get() = myTargetLocators

    private val isDebugDrawing: Boolean
        get() = myDebugDrawing.get()

    init {
        myLayers = ArrayList(layers)

        moveTo(myLayoutInfo.getAbsoluteBounds(myTilesOrigin).origin)
    }

    override fun buildComponent() {
        /*
    // Don't set this flag: it was harmless when we were using SvgNodeSubtreeGeneratingSynchronizer but with new
    // SvgNodeSubtreeBufferGeneratingSynchronizer this leads to having all svg event handlers ignored
    // because the entire plot panel will be generated to a string buffer.
    // We want event handlers to be called on SvgElement-s
    getRootGroup().setPrebuiltSubtree(true);
    */

        val geomBounds = myLayoutInfo.geomBounds

        if (myTheme.plot().showInnerFrame()) {
            val rect = SvgRectElement(geomBounds).apply {
                strokeColor().set(myTheme.plot().innerFrameColor())
                strokeWidth().set(1.0)
                fillOpacity().set(0.0)
            }
            add(rect)
        }

        addFacetLabels(geomBounds, myTheme.facets())

        val liveMapGeomLayer = myLayers.firstOrNull { it.isLiveMap }
        if (liveMapGeomLayer == null && myShowAxis) {
            addAxis(geomBounds)
        }

        if (isDebugDrawing) {
            val tileBounds = myLayoutInfo.bounds
            val rect = SvgRectElement(tileBounds)
            rect.fillColor().set(Color.BLACK)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.1)
            add(rect)
        }

        if (isDebugDrawing) {
            val clipBounds = myLayoutInfo.clipBounds
            val rect = SvgRectElement(clipBounds)
            rect.fillColor().set(Color.DARK_GREEN)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.3)
            add(rect)
        }

        if (isDebugDrawing) {
            val rect = SvgRectElement(geomBounds)
            rect.fillColor().set(Color.PINK)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.5)
            add(rect)
        }

        // render geoms

        if (liveMapGeomLayer != null) {
            val realBounds = myLayoutInfo.getAbsoluteGeomBounds(myTilesOrigin)

            val liveMapData = liveMapGeomLayer.createCanvasFigure(realBounds)

            liveMapFigure = liveMapData.canvasFigure
            myTargetLocators.add(liveMapData.targetLocator)
        } else {
            // normal plot tile
            val sharedNumericMappers = HashMap<Aes<Double>, (Double?) -> Double?>()
            val overallNumericDomains = HashMap<Aes<Double>, ClosedRange<Double>>()

            val xAxisInfo = myLayoutInfo.xAxisInfo
            val yAxisInfo = myLayoutInfo.yAxisInfo
            val mapperX = myScaleX.mapper
            val mapperY = myScaleY.mapper

            sharedNumericMappers[Aes.X] = mapperX
            sharedNumericMappers[Aes.Y] = mapperY
            sharedNumericMappers[Aes.SLOPE] = Mappers.mul(mapperY(1.0)!! / mapperX(1.0)!!)

            overallNumericDomains[Aes.X] = xAxisInfo!!.axisDomain!!
            overallNumericDomains[Aes.Y] = yAxisInfo!!.axisDomain!!

            val geomLayerComponents = buildGeoms(sharedNumericMappers, overallNumericDomains, myCoord)
            for (layerComponent in geomLayerComponents) {
                layerComponent.moveTo(geomBounds.origin)

                val xRange = myCoord.xClientLimit ?: ClosedRange(0.0, geomBounds.width)
                val yRange = myCoord.yClientLimit ?: ClosedRange(0.0, geomBounds.height)
                val clipRect = GeometryUtil.doubleRange(xRange, yRange)

                layerComponent.clipBounds(clipRect)
                add(layerComponent)
            }
        }
    }

    private fun addFacetLabels(geomBounds: DoubleRectangle, theme: FacetsTheme) {
        // facet X label (on top of geom area)
        val xLabels = myLayoutInfo.facetXLabels
        if (xLabels.isNotEmpty()) {
            val labelSize = facetColLabelSize(geomBounds.width)
            val labelOrig = DoubleVector(
                geomBounds.left + FACET_H_PADDING,
                geomBounds.top - facetColHeadHeight(xLabels.size) + FACET_V_PADDING
            )
            var labelBounds = DoubleRectangle(
                labelOrig, labelSize
            )
            for (xLabel in xLabels) {
                val rect = SvgRectElement(labelBounds).apply {
                    strokeWidth().set(0.0)
                    fillColor().set(theme.labelBackground())

//                    strokeWidth().set(1.0)
//                    strokeColor().set(Color.BLACK)
//                    fillOpacity().set(0.0)
                }
                add(rect)

                val x = labelBounds.center.x
                val y = labelBounds.center.y
                val lab = TextLabel(xLabel)
                lab.moveTo(x, y)
                lab.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
                lab.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
                add(lab)

                labelBounds = labelBounds.add(DoubleVector(0.0, labelSize.y))
            }
        }

        // facet Y label (to the right from geom area)
        if (myLayoutInfo.facetYLabel != null) {

            val hPad = FACET_V_PADDING
            val vPad = FACET_H_PADDING

            val labelBounds = DoubleRectangle(
                geomBounds.right + hPad, geomBounds.top - vPad,
                FACET_TAB_HEIGHT - hPad * 2, geomBounds.height - vPad * 2
            )
            val rect = SvgRectElement(labelBounds)
            rect.strokeWidth().set(0.0)
            rect.fillColor().set(theme.labelBackground())
            add(rect)

            val x = labelBounds.center.x
            val y = labelBounds.center.y

            val lab = TextLabel(myLayoutInfo.facetYLabel)
            lab.moveTo(x, y)
            lab.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
            lab.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
            lab.rotate(90.0)
            add(lab)
        }
    }

    private fun addAxis(geomBounds: DoubleRectangle) {
        // X axis (below geom area)
        if (myLayoutInfo.xAxisShown) {
            val axis = buildAxis(myScaleX, myLayoutInfo.xAxisInfo!!, myCoord, myTheme.axisX())
            axis.moveTo(DoubleVector(geomBounds.left, geomBounds.bottom))
            add(axis)
        }
        // Y axis (to the left from geom area, axis elements have negative x-positions)
        if (myLayoutInfo.yAxisShown) {
            val axis = buildAxis(myScaleY, myLayoutInfo.yAxisInfo!!, myCoord, myTheme.axisY())
            axis.moveTo(geomBounds.origin)
            add(axis)
        }
    }

    private fun buildAxis(
        scale: Scale<Double>,
        info: AxisLayoutInfo,
        coord: CoordinateSystem,
        theme: AxisTheme
    ): AxisComponent {
        val axis =
            AxisComponent(info.axisLength, info.orientation!!)
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

    private fun buildGeoms(
        sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
        overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>,
        coord: CoordinateSystem
    ): List<SvgComponent> {

        val layerRenderers = ArrayList<SvgComponent>()
        for (layer in myLayers) {
            val rendererData = LayerRendererUtil.createLayerRendererData(
                layer,
                sharedNumericMappers,
                overallNumericDomains
            )

            val aestheticMappers = rendererData.aestheticMappers
            val aesthetics = rendererData.aesthetics

            val targetCollector = LayerTargetCollectorWithLocator(
                layer.geomKind,
                layer.locatorLookupSpec,
                layer.contextualMapping,
                coord
            )
            myTargetLocators.add(targetCollector)

            val ctx = GeomContextBuilder()
                .aesthetics(aesthetics)
                .aestheticMappers(aestheticMappers)
                .geomTargetCollector(targetCollector)
                .build()

            val pos = rendererData.pos
            val geom = layer.geom

            layerRenderers.add(SvgLayerRenderer(aesthetics, geom, pos, coord, ctx))
        }
        return layerRenderers
    }

    fun setShowAxis(showAxis: Boolean) {
        myShowAxis = showAxis
    }

    fun debugDrawing(): Property<Boolean> {
        return myDebugDrawing
    }
}

private fun GeomLayer.createCanvasFigure(bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
    return (geom as LiveMapGeom).createCanvasFigure(bounds)
}
