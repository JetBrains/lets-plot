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
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil.marginalLayersByMargin
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout.Companion.FACET_PADDING
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout.Companion.facetColHeadTotalHeight
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.LayerTargetCollectorWithLocator
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformBuilder

internal class PlotTile(
    private val coreLayers: List<GeomLayer>,
    private val marginalLayers: List<GeomLayer>,
    private val tilesOrigin: DoubleVector,
    private val tileLayoutInfo: TileLayoutInfo,
    private val theme: Theme,
    private val frameOfReference: FrameOfReference,
    private val marginalFrameByMargin: Map<MarginSide, FrameOfReference>
) : SvgComponent() {

    val interactionSupport = InteractionSupport()

    private val frameBottomGroup = GroupComponent()
    private val clipGroup = GroupComponent()
    private val geomGroup = GroupComponent()
    private val geomInteractionGroup = GroupComponent() // to set interaction transformations
    private val frameTopGroup = GroupComponent()

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

        add(frameBottomGroup)
        add(clipGroup)
        clipGroup.add(geomGroup)
        geomGroup.moveTo(tileLayoutInfo.geomContentBounds.origin)
        geomGroup.add(geomInteractionGroup)
        add(frameTopGroup)


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

            for (layer in coreLayers) {
                val collectorWithLocator = LayerTargetCollectorWithLocator(
                    layer.geomKind,
                    layer.locatorLookupSpec,
                    layer.createContextualMapping(),
                )
                _targetLocators.add(collectorWithLocator)

                val layerComponent = frameOfReference.buildGeomComponent(layer, collectorWithLocator)
                geomInteractionGroup.add(layerComponent.rootGroup)
                frameOfReference.setClip(clipGroup)
            }

            // Marginal layers
            val marginalLayersByMargin: Map<MarginSide, List<GeomLayer>> = marginalLayersByMargin(marginalLayers)

            for ((margin, layers) in marginalLayersByMargin) {
                val marginFrame = marginalFrameByMargin.getValue(margin)
                for (layer in layers) {
                    val marginComponent = marginFrame.buildGeomComponent(layer, NullGeomTargetCollector())
                    add(marginComponent)
                    marginFrame.setClip(marginComponent)
                }
            }

            frameOfReference.drawBeforeGeomLayer(frameBottomGroup)
            frameOfReference.drawAfterGeomLayer(frameTopGroup)
        }
    }

    private fun addFacetLabels(geomBounds: DoubleRectangle, theme: FacetsTheme) {
//        if (!theme.showStrip()) return

        // facet X label (on top of geom area)
        val xLabels = tileLayoutInfo.facetXLabels
        if (xLabels.isNotEmpty()) {
            val totalHeadHeight = tileLayoutInfo.facetXLabels.map { it.second }.let(::facetColHeadTotalHeight)
            val labelOrig = DoubleVector(
                geomBounds.left,
                geomBounds.top - totalHeadHeight
            )
            var curLabelOrig = labelOrig
            xLabels.forEach { (xLabel, labHeight) ->
                val labelBounds = DoubleRectangle(
                    curLabelOrig,
                    DoubleVector(geomBounds.width, labHeight)
                )

                // ToDo: Use "facet X" theme.
                addFacetLabBackground(labelBounds, theme)

                addLabelElement(labelBounds, theme, xLabel, isColumnLabel = true)

                curLabelOrig = curLabelOrig.add(DoubleVector(0.0, labHeight))
            }
        }

        // facet Y label (to the right from geom area)
        if (tileLayoutInfo.facetYLabel != null) {
            val (yLabel, labWidth) = tileLayoutInfo.facetYLabel

            val labelBounds = DoubleRectangle(
                geomBounds.right + FACET_PADDING,
                geomBounds.top,
                labWidth,
                geomBounds.height
            )

            // ToDo: Use "facet Y" theme.
            addFacetLabBackground(labelBounds, theme)

            addLabelElement(labelBounds, theme, yLabel, isColumnLabel = false)
        }
    }

    private fun addFacetLabBackground(labelBounds: DoubleRectangle, facetTheme: FacetsTheme) {
        if (facetTheme.showStripBackground()) {
            val rect = SvgRectElement(labelBounds).apply {
                strokeWidth().set(facetTheme.stripStrokeWidth())
                fillColor().set(facetTheme.stripFill())
                strokeColor().set(facetTheme.stripColor())
                StrokeDashArraySupport.apply(this, facetTheme.stripStrokeWidth(), facetTheme.stripLineType())
            }
            add(rect)
        }
    }

    private fun addLabelElement(
        labelBounds: DoubleRectangle,
        theme: FacetsTheme,
        label: String,
        isColumnLabel: Boolean
    ) {
        val textBounds = theme.stripMargins().shrinkRect(labelBounds)
        if (DEBUG_DRAWING) {
            val rect = SvgRectElement(textBounds).apply {
                strokeWidth().set(1.0)
                fillOpacity().set(0.0)
                strokeColor().set(Color.MAGENTA)
            }
            add(rect)
        }

        val textSize = FacetedPlotLayout.titleSize(label, theme)
        val labelSpec = PlotLabelSpecFactory.facetText(theme)
        val lineHeight = labelSpec.height()
        val className = if (isColumnLabel) "x" else "y"
        val rotation = if (isColumnLabel) null else TextRotation.CLOCKWISE

        val lab = MultilineLabel(label)
        lab.addClassName("${Style.FACET_STRIP_TEXT}-$className")

        val (pos, hAnchor) = applyJustification(
            textBounds,
            textSize,
            lineHeight,
            theme.stripTextJustification(),
            rotation
        )
        lab.setHorizontalAnchor(hAnchor)
        lab.setLineHeight(lineHeight)
        lab.moveTo(pos)
        rotation?.let { lab.rotate(it.angle) }

        add(lab)
    }

    companion object {
        private fun createCanvasFigure(layer: GeomLayer, bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
            return (layer.geom as LiveMapGeom).createCanvasFigure(bounds)
        }

        private const val DEBUG_DRAWING = PLOT_DEBUG_DRAWING
    }

    inner class InteractionSupport {
        private var scale = 1.0
        private var pan = DoubleVector.ZERO

        fun pan(from: DoubleVector, to: DoubleVector): DoubleVector? {
            val offset = to.subtract(from).mul(1 / this.scale).add(pan)

            val domainOffset = frameOfReference.pan(DoubleVector.ZERO, offset)
            geomInteractionGroup.rootGroup.transform().set(SvgTransformBuilder()
                .translate(offset.mul(this.scale))
                .scale(scale)
                .build()
            )
            frameBottomGroup.clear()
            frameOfReference.drawBeforeGeomLayer(frameBottomGroup)
            frameTopGroup.clear()
            frameOfReference.drawAfterGeomLayer(frameTopGroup)

            return domainOffset
        }

        fun panEnd(from: DoubleVector, to: DoubleVector): DoubleVector? {
            val offset = to.subtract(from).mul(1 / this.scale)
            pan = pan.add(offset)

            val domainOffset = frameOfReference.pan(DoubleVector.ZERO, pan)
            geomInteractionGroup.rootGroup.transform().set(SvgTransformBuilder()
                .translate(pan.mul( this.scale))
                .scale(scale)
                .build()
            )
            frameBottomGroup.clear()
            frameOfReference.drawBeforeGeomLayer(frameBottomGroup)
            frameTopGroup.clear()
            frameOfReference.drawAfterGeomLayer(frameTopGroup)

            return domainOffset
        }

        fun zoom(offset: DoubleVector, scale: DoubleVector) {

            //frameOfReference.pan(tileLayoutInfo.geomContentBounds.origin, tileLayoutInfo.geomContentBounds.origin.add(offset))
            pan = pan.add(offset.mul(1 / this.scale))
            val scaleUpdate = maxOf(scale.x, scale.y)
            this.scale *= scaleUpdate
            frameOfReference.zoom(scaleUpdate)

            val transform = SvgTransformBuilder()
                .translate(pan.mul( this.scale))
                .scale(this.scale)
                .build()
            println("transform: $transform")

            geomInteractionGroup.rootGroup.transform().set(
                transform
            )
            frameBottomGroup.clear()
            frameOfReference.drawBeforeGeomLayer(frameBottomGroup)
            frameTopGroup.clear()
            frameOfReference.drawAfterGeomLayer(frameTopGroup)

        }

    }
}
