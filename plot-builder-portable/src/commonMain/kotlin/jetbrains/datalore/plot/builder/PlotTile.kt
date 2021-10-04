/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.base.geom.LiveMapGeom
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.interact.loc.LayerTargetCollectorWithLocator
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.FACET_H_PADDING
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.FACET_TAB_HEIGHT
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.FACET_V_PADDING
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.facetColHeadHeight
import jetbrains.datalore.plot.builder.layout.FacetGridPlotLayout.Companion.facetColLabelSize
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgRectElement

internal class PlotTile(
    private val layers: List<GeomLayer>,
    private val tilesOrigin: DoubleVector,
    private val tileLayoutInfo: TileLayoutInfo,
    private val theme: Theme,
    private val frameOfReference: TileFrameOfReference,
) : SvgComponent() {

    var isDebugDrawing: Boolean = false

    private val myTargetLocators = ArrayList<GeomTargetLocator>()

    var liveMapFigure: SomeFig? = null
        private set

    val targetLocators: List<GeomTargetLocator>
        get() = myTargetLocators

    lateinit var geomDrawingBounds: DoubleRectangle  // the area between axes or x/y limits
        private set

    init {
        moveTo(tileLayoutInfo.getAbsoluteBounds(tilesOrigin).origin)
    }

    override fun buildComponent() {
        /*
    // Don't set this flag: it was harmless when we were using SvgNodeSubtreeGeneratingSynchronizer but with new
    // SvgNodeSubtreeBufferGeneratingSynchronizer this leads to having all svg event handlers ignored
    // because the entire plot panel will be generated to a string buffer.
    // We want event handlers to be called on SvgElement-s
    getRootGroup().setPrebuiltSubtree(true);
    */

        val geomBounds = tileLayoutInfo.geomBounds

//        if (theme.plot().showInnerFrame()) {
        if (theme.panel().show()) {
            val rect = SvgRectElement(geomBounds).apply {
//                strokeColor().set(theme.plot().innerFrameColor())
                strokeColor().set(theme.panel().color())
//                strokeWidth().set(1.0)
//                fillOpacity().set(0.0)
                strokeWidth().set(theme.panel().size())
                fillColor().set(theme.panel().fill())
            }
            add(rect)
        }

        addFacetLabels(geomBounds, theme.facets())

        // render geoms

        val liveMapGeomLayer = layers.firstOrNull { it.isLiveMap }
        if (liveMapGeomLayer != null) {
            val realBounds = tileLayoutInfo.getAbsoluteGeomBounds(tilesOrigin)
            val liveMapData = createCanvasFigure(liveMapGeomLayer, realBounds)

            liveMapFigure = liveMapData.canvasFigure
            myTargetLocators.add(liveMapData.targetLocator)
            geomDrawingBounds = DoubleRectangle(ZERO, geomBounds.dimension)
        } else {
            // Normal plot tiles

            frameOfReference.drawAxis(this)
            geomDrawingBounds = frameOfReference.applyClientLimits(DoubleRectangle(ZERO, geomBounds.dimension))

            for (layer in layers) {
                val collectorWithLocator = LayerTargetCollectorWithLocator(
                    layer.geomKind,
                    layer.locatorLookupSpec,
                    layer.contextualMapping,
                )
                myTargetLocators.add(collectorWithLocator)

                val layerComponent = frameOfReference.buildGeomComponent(layer, collectorWithLocator)
                layerComponent.moveTo(geomBounds.origin)
                layerComponent.clipBounds(geomDrawingBounds)
                add(layerComponent)
            }
        }
    }

    private fun addFacetLabels(geomBounds: DoubleRectangle, theme: FacetsTheme) {
        // facet X label (on top of geom area)
        val xLabels = tileLayoutInfo.facetXLabels
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
        if (tileLayoutInfo.facetYLabel != null) {

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

            val lab = TextLabel(tileLayoutInfo.facetYLabel)
            lab.moveTo(x, y)
            lab.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
            lab.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
            lab.rotate(90.0)
            add(lab)
        }
    }

    companion object {
        private fun createCanvasFigure(layer: GeomLayer, bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
            return (layer.geom as LiveMapGeom).createCanvasFigure(bounds)
        }
    }
}
