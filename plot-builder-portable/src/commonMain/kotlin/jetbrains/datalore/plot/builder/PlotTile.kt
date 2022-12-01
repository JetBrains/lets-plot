/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.base.geom.LiveMapGeom
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.MarginalLayerUtil.marginalLayersByMargin
import jetbrains.datalore.plot.builder.interact.loc.LayerTargetCollectorWithLocator
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayout.Companion.FACET_H_PADDING
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayout.Companion.FACET_TAB_HEIGHT
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayout.Companion.FACET_V_PADDING
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayout.Companion.facetColHeadHeight
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayout.Companion.facetColLabelSize
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgRectElement

internal class PlotTile(
    private val coreLayers: List<GeomLayer>,
    private val marginalLayers: List<GeomLayer>,
    private val tilesOrigin: DoubleVector,
    private val tileLayoutInfo: TileLayoutInfo,
    private val theme: Theme,
    private val frameOfReference: FrameOfReference,
    private val marginalFrameByMargin: Map<MarginSide, FrameOfReference>
) : SvgComponent() {

    private val _targetLocators = ArrayList<GeomTargetLocator>()

    var liveMapFigure: SomeFig? = null
        private set

    val targetLocators: List<GeomTargetLocator>
        get() = _targetLocators

    val layerYOrientations: List<Boolean> = coreLayers.map { it.isYOrientation }

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

        val geomOuterBounds = tileLayoutInfo.geomOuterBounds

        addFacetLabels(geomOuterBounds, theme.facets())

        // render geoms

        val liveMapGeomLayer = coreLayers.firstOrNull { it.isLiveMap }
        if (liveMapGeomLayer != null) {
            val realBounds = tileLayoutInfo.getAbsoluteOuterGeomBounds(tilesOrigin)
            val liveMapData = createCanvasFigure(liveMapGeomLayer, realBounds)

            liveMapFigure = liveMapData.canvasFigure
            _targetLocators.add(liveMapData.targetLocator)
        } else {
            // Normal plot tiles

            frameOfReference.drawBeforeGeomLayer(this)

            for (layer in coreLayers) {
                val collectorWithLocator = LayerTargetCollectorWithLocator(
                    layer.geomKind,
                    layer.locatorLookupSpec,
                    layer.createContextualMapping(),
                )
                _targetLocators.add(collectorWithLocator)

                val layerComponent = frameOfReference.buildGeomComponent(layer, collectorWithLocator)
                add(layerComponent)
            }

            // Marginal layers
            val marginalLayersByMargin: Map<MarginSide, List<GeomLayer>> = marginalLayersByMargin(marginalLayers)

            for ((margin, layers) in marginalLayersByMargin) {
                val marginFrame = marginalFrameByMargin.getValue(margin)
                for (layer in layers) {
                    val marginComponent = marginFrame.buildGeomComponent(layer, NullGeomTargetCollector())
                    add(marginComponent)
                }
            }

            frameOfReference.drawAfterGeomLayer(this)
        }
    }

    private fun addFacetLabels(geomBounds: DoubleRectangle, theme: FacetsTheme) {
//        if (!theme.showStrip()) return

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
                // ToDo: Use "facet X" theme.
                addFacetLabBackground(labelBounds, theme)

                val x = labelBounds.center.x
                val y = labelBounds.center.y
                val lab = TextLabel(xLabel)
                lab.addClassName("${Style.FACET_STRIP_TEXT}-x")
                lab.moveTo(x, y)
                lab.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
                lab.setVerticalAnchor(Text.VerticalAnchor.CENTER)
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

            // ToDo: Use "facet Y" theme.
            addFacetLabBackground(labelBounds, theme)

            val x = labelBounds.center.x
            val y = labelBounds.center.y

            val lab = TextLabel(tileLayoutInfo.facetYLabel)
            lab.addClassName("${Style.FACET_STRIP_TEXT}-y")
            lab.moveTo(x, y)
            lab.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            lab.setVerticalAnchor(Text.VerticalAnchor.CENTER)
            lab.rotate(90.0)
            add(lab)
        }
    }

    private fun addFacetLabBackground(labelBounds: DoubleRectangle, facetTheme: FacetsTheme) {
        if (facetTheme.showStripBackground()) {
            val rect = SvgRectElement(labelBounds).apply {
                strokeWidth().set(facetTheme.stripStrokeWidth())
                fillColor().set(facetTheme.stripFill())
                strokeColor().set(facetTheme.stripColor())
            }
            add(rect)
        }
    }

    companion object {
        private fun createCanvasFigure(layer: GeomLayer, bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
            return (layer.geom as LiveMapGeom).createCanvasFigure(bounds)
        }
    }
}
