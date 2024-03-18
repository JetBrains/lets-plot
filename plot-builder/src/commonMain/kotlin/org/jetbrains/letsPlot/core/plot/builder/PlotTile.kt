/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.FeatureSwitch.PLOT_DEBUG_DRAWING
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapGeom
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapProvider
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.TextRotation
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.applyJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil.marginalLayersByMargin
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout.Companion.FACET_PADDING
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout.Companion.facetColHeadTotalHeight
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout.Companion.facetLabelSize
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.LayerTargetCollectorWithLocator
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

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

    val layerYOrientations: List<Boolean> = coreLayers.map(GeomLayer::isYOrientation)

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

        val liveMapGeomLayer = coreLayers.firstOrNull(GeomLayer::isLiveMap)
        if (liveMapGeomLayer != null) {
            val realBounds = tileLayoutInfo.getAbsoluteOuterGeomBounds(tilesOrigin)
            val liveMapData = createCanvasFigure(liveMapGeomLayer, realBounds)

            liveMapFigure = liveMapData.canvasFigure
            _targetLocators.addAll(liveMapData.targetLocators)
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
            val totalHeadHeight = tileLayoutInfo.facetXTabHeights?.let(::facetColHeadTotalHeight)
                ?: facetColHeadTotalHeight(xLabels, theme)
            val labelOrig = DoubleVector(
                geomBounds.left,
                geomBounds.top - totalHeadHeight
            )
            var curLabelOrig = labelOrig
            xLabels.forEachIndexed { index, xLabel ->
                val labHeight = tileLayoutInfo.facetXTabHeights?.get(index)
                    ?: facetLabelSize(xLabel, theme, marginSize = Thickness::height)
                val labelBounds = DoubleRectangle(
                    curLabelOrig,
                    DoubleVector(geomBounds.width,  labHeight)
                )

                // ToDo: Use "facet X" theme.
                addFacetLabBackground(labelBounds, theme)

                // without margins
                val textBounds = DoubleRectangle(
                    labelBounds.left,
                    labelBounds.top + theme.stripMargins().top,
                    labelBounds.width,
                    labelBounds.height - theme.stripMargins().height()
                )
                if (DEBUG_DRAWING) {
                    drawDebugRect(textBounds, Color.MAGENTA)
                 }

                val textSize = FacetedPlotLayout.titleSize(xLabel, theme)
                val labelSpec = PlotLabelSpecFactory.facetText(theme)
                val lineHeight = labelSpec.height()

                val lab = MultilineLabel(xLabel)
                lab.addClassName("${Style.FACET_STRIP_TEXT}-x")

                val (pos, hAnchor) = applyJustification(
                    textBounds,
                    textSize,
                    lineHeight,
                    theme.stripTextJustification()
                )
                lab.setHorizontalAnchor(hAnchor)
                lab.setLineHeight(lineHeight)
                lab.moveTo(pos)
                add(lab)

                curLabelOrig = curLabelOrig.add(DoubleVector(0.0, labelBounds.height))
            }
        }

        // facet Y label (to the right from geom area)
        if (tileLayoutInfo.facetYLabel != null) {
            val headWidth = tileLayoutInfo.facetYTabWidth
                ?: facetLabelSize(tileLayoutInfo.facetYLabel, theme, Thickness::width)

            val labelBounds = DoubleRectangle(
                geomBounds.right + FACET_PADDING,
                geomBounds.top,
                headWidth,
                geomBounds.height
            )

            // ToDo: Use "facet Y" theme.
            addFacetLabBackground(labelBounds, theme)

            // without margins
            val textBounds = DoubleRectangle(
                labelBounds.left + theme.stripMargins().left,
                labelBounds.top,
                labelBounds.width - theme.stripMargins().width(),
                labelBounds.height
            )
            if (DEBUG_DRAWING) {
                drawDebugRect(textBounds, Color.MAGENTA)
            }

            val textSize = FacetedPlotLayout.titleSize(tileLayoutInfo.facetYLabel, theme)
            val labelSpec = PlotLabelSpecFactory.facetText(theme)
            val lineHeight = labelSpec.height()
            val rotation = TextRotation.CLOCKWISE

            val lab = MultilineLabel(tileLayoutInfo.facetYLabel)
            lab.addClassName("${Style.FACET_STRIP_TEXT}-y")
            val (pos, hAnchor) = applyJustification(
                textBounds,
                textSize,
                lineHeight,
                theme.stripTextJustification(),
                rotation
            )
            lab.setHorizontalAnchor(hAnchor)
            lab.setLineHeight(lineHeight)
            lab.setHorizontalAnchor(hAnchor)
            lab.moveTo(pos)
            lab.rotate(rotation.angle)
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

    private fun drawDebugRect(r: DoubleRectangle, color: Color) {
        val rect = SvgRectElement(r)
        rect.strokeColor().set(color)
        rect.strokeWidth().set(1.0)
        rect.fillOpacity().set(0.0)
        add(rect)
    }

    companion object {
        private fun createCanvasFigure(layer: GeomLayer, bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
            return (layer.geom as LiveMapGeom).createCanvasFigure(bounds)
        }

        private const val DEBUG_DRAWING = PLOT_DEBUG_DRAWING
    }
}
