/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.FeatureSwitch.PLOT_DEBUG_DRAWING
import org.jetbrains.letsPlot.core.canvas.CanvasDrawable
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.UnsupportedInteractionException
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapGeom
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapProvider
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.TextRotation
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.applyJustification
import org.jetbrains.letsPlot.core.plot.base.render.svg.*
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.core.plot.base.theme.FacetStripTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.NullGeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.NullGeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.LayerTargetCollectorWithLocator
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil.marginalLayersByMargin
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayout.Companion.facetColHeadTotalHeight
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformBuilder
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

internal class PlotTile constructor(
    private val coreLayers: List<GeomLayer>,
    private val marginalLayers: List<GeomLayer>,
    private val tilesOrigin: DoubleVector,
    private val tileLayoutInfo: TileLayoutInfo,
    val plotSpecId: String?,
    private val theme: Theme,
    private val frameOfReference: FrameOfReference,
    private val marginalFrameByMargin: Map<MarginSide, FrameOfReference>
) : SvgComponent() {

    private val clipGroup = GroupComponent()
    private val geomGroup = GroupComponent()
    private val geomInteractionGroup = GroupComponent() // to set interaction transformations

    val transientState = TransientState()

    private val _targetLocators = ArrayList<GeomTargetLocator>()

    var liveMapCanvasDrawable: CanvasDrawable? = null
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

        add(frameOfReference.bottomGroup)
        add(clipGroup)
        clipGroup.add(geomGroup)
        geomGroup.moveTo(tileLayoutInfo.geomContentBounds.origin)
        geomGroup.add(geomInteractionGroup)
        add(frameOfReference.topGroup)

        if (DEBUG_DRAWING) {
            drawDebugRect(
                r = tileLayoutInfo.geomContentBounds,
                color = Color.GREEN,
                strokeWidth = 2.0
            )
        }

        val geomOuterBounds = tileLayoutInfo.geomOuterBounds

        addFacetLabels(geomOuterBounds, theme.facets())

        // render geoms

        val liveMapGeomLayer = coreLayers.firstOrNull(GeomLayer::isLiveMap)
        if (liveMapGeomLayer != null) {
            val realBounds = tileLayoutInfo.getAbsoluteGeomOuterBounds(tilesOrigin)
            val liveMapData = createCanvasFigure(liveMapGeomLayer, realBounds)

            liveMapCanvasDrawable = liveMapData.canvasDrawable
            _targetLocators.addAll(liveMapData.targetLocators)
        } else {
            // Normal plot tiles

            for (layer in coreLayers) {
                // skip layer
                val collectorWithLocator = layer.createContextualMapping()?.let {
                    LayerTargetCollectorWithLocator(layer.geomKind, it)
                } ?: object :
                    GeomTargetLocator by NullGeomTargetLocator,
                    GeomTargetCollector by NullGeomTargetCollector {
                }

                _targetLocators.add(collectorWithLocator)

                val layerComponent = frameOfReference.buildGeomComponent(layer, collectorWithLocator)
                layerComponent.rootGroup.setAttribute("buffered-rendering", "static")
                geomInteractionGroup.add(layerComponent.rootGroup)
                frameOfReference.setClip(clipGroup)
            }

            // Marginal layers
            val marginalLayersByMargin: Map<MarginSide, List<GeomLayer>> = marginalLayersByMargin(marginalLayers)

            for ((margin, layers) in marginalLayersByMargin) {
                val marginFrame = marginalFrameByMargin.getValue(margin)
                for (layer in layers) {
                    val marginComponent = marginFrame.buildGeomComponent(layer, NullGeomTargetCollector)
                    add(marginComponent)
                    marginFrame.setClip(marginComponent)
                }
            }

            frameOfReference.repaintFrame()
        }
    }

    private fun addFacetLabels(geomBounds: DoubleRectangle, theme: FacetsTheme) {
        // facet X label (on top of geom area)
        addHorizontalFacetLabels(geomBounds, theme.horizontalFacetStrip())

        // facet Y label (to the right from geom area)
        addVerticalFacetLabels(geomBounds, theme.verticalFacetStrip())
    }

    private fun addHorizontalFacetLabels(geomBounds: DoubleRectangle, theme: FacetStripTheme) {
        if (!theme.showStrip() || tileLayoutInfo.facetXLabels.isEmpty()) {
            return
        }

        val totalHeadHeight =
            facetColHeadTotalHeight(
                tileLayoutInfo.facetXLabels.map { it.second },
                theme.stripSpacing().y
            )
        val labelOrig = DoubleVector(
            geomBounds.left,
            geomBounds.top - totalHeadHeight
        )
        var curLabelOrig = labelOrig
        tileLayoutInfo.facetXLabels.forEach { (xLabel, labHeight) ->
            val labelBounds = DoubleRectangle(
                curLabelOrig,
                DoubleVector(geomBounds.width, labHeight)
            )

            addFacetLabBackground(labelBounds, theme)

            addLabelElement(labelBounds, theme, xLabel, isColumnLabel = true)

            curLabelOrig = curLabelOrig.add(DoubleVector(0.0, labHeight))
        }
    }

    private fun addVerticalFacetLabels(geomBounds: DoubleRectangle, theme: FacetStripTheme) {
        if (!theme.showStrip() || tileLayoutInfo.facetYLabel == null) {
            return
        }

        val (yLabel, labWidth) = tileLayoutInfo.facetYLabel

        val labelBounds = DoubleRectangle(
            geomBounds.right + theme.stripSpacing().x,
            geomBounds.top,
            labWidth,
            geomBounds.height
        )

        addFacetLabBackground(labelBounds, theme)

        addLabelElement(labelBounds, theme, yLabel, isColumnLabel = false)
    }

    private fun addFacetLabBackground(labelBounds: DoubleRectangle, facetTheme: FacetStripTheme) {
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
        theme: FacetStripTheme,
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

        val labelSpec = PlotLabelSpecFactory.facetText(theme)
        val measuredText = labelSpec.layout(label)
        val textLayout = measuredText.layout
        val className = if (isColumnLabel) "x" else "y"
        val themeAngle = theme.stripTextAngle()
        val defaultRotation = if (isColumnLabel) null else TextRotation.CLOCKWISE
        val rotation = if (themeAngle.isNaN()) {
            defaultRotation
        } else when (themeAngle) {
            90.0 -> TextRotation.CLOCKWISE
            -90.0, 270.0 -> TextRotation.ANTICLOCKWISE
            0.0 -> null
            else -> defaultRotation  // Fallback for unsupported angles
        }

        val lab = Label(label)
        lab.addClassName("${Style.FACET_STRIP_TEXT}-$className")
        val fontSize = theme.stripTextStyle().size

        // ToDo:  angle normalization
        val hasSupportedRightAngle = themeAngle in setOf(0.0, 90.0, -90.0, 270.0)
        val (pos, hAnchor) = if (themeAngle.isNaN() || hasSupportedRightAngle) {
            applyJustification(
                textBounds,
                fontSize,
                textLayout,
                theme.stripTextJustification(),
                rotation
            )
        } else {
            applyRotatedJustification(
                textBounds,
                measuredText.totalSize,
                fontSize,
                textLayout,
                theme.stripTextJustification(),
                themeAngle
            )
        }
        lab.setHorizontalAnchor(hAnchor)
        lab.setFontSize(labelSpec.font.size.toDouble())
        val face = theme.stripTextStyle().face
        if (face.bold) lab.setFontWeight(face.weight)
        if (face.italic) lab.setFontStyle(face.style)
        lab.setTextLayout(textLayout)
        lab.moveTo(pos)
        if (!themeAngle.isNaN() && themeAngle != 0.0) {
            lab.rotate(themeAngle)
        } else {
            rotation?.let { lab.rotate(it.angle) }
        }

        add(lab)
    }

    private fun applyRotatedJustification(
        boundRect: DoubleRectangle,
        textSize: DoubleVector,
        fontSize: Double,
        textLayout: TextBlockLayout,
        justification: TextJustification,
        angle: Double
    ): Pair<DoubleVector, Text.HorizontalAnchor> {
        val hAnchor = when {
            justification.x < 0.5 -> Text.HorizontalAnchor.LEFT
            justification.x == 0.5 -> Text.HorizontalAnchor.MIDDLE
            else -> Text.HorizontalAnchor.RIGHT
        }

        val localLeft = when (hAnchor) {
            Text.HorizontalAnchor.LEFT -> 0.0
            Text.HorizontalAnchor.MIDDLE -> -textSize.x / 2.0
            Text.HorizontalAnchor.RIGHT -> -textSize.x
        }
        val localRight = localLeft + textSize.x

        val baselineAtTop = applyJustification(
            DoubleRectangle(0.0, 0.0, textSize.x, textSize.y),
            fontSize,
            textLayout,
            TextJustification(0.0, 1.0)
        ).first.y
        val localTop = -baselineAtTop
        val localBottom = localTop + textSize.y

        val rotatedBounds = rotatedBounds(
            listOf(
                DoubleVector(localLeft, localTop),
                DoubleVector(localRight, localTop),
                DoubleVector(localRight, localBottom),
                DoubleVector(localLeft, localBottom)
            ),
            angle
        )

        val targetLeft = boundRect.left + (boundRect.width - rotatedBounds.width) * justification.x
        val targetTop = boundRect.top + (boundRect.height - rotatedBounds.height) * (1.0 - justification.y)
        val origin = DoubleVector(
            targetLeft - rotatedBounds.left,
            targetTop - rotatedBounds.top
        )
        return origin to hAnchor
    }

    private fun rotatedBounds(points: List<DoubleVector>, angle: Double): DoubleRectangle {
        val radians = toRadians(angle)
        val sin = sin(radians)
        val cos = cos(radians)
        val rotatedPoints = points.map { p ->
            DoubleVector(
                p.x * cos - p.y * sin,
                p.x * sin + p.y * cos
            )
        }
        val left = rotatedPoints.minOf(DoubleVector::x)
        val right = rotatedPoints.maxOf(DoubleVector::x)
        val top = rotatedPoints.minOf(DoubleVector::y)
        val bottom = rotatedPoints.maxOf(DoubleVector::y)
        return DoubleRectangle(left, top, max(0.0, right - left), max(0.0, bottom - top))
    }

    /**
     * Throws UnsupportedInteractionException if not supported
     */
    fun checkMouseInteractionSupported(eventSpec: MouseEventSpec) {
        if (liveMapCanvasDrawable != null) {
            throw UnsupportedInteractionException("$eventSpec denied by LiveMap component.")
        }
        frameOfReference.checkMouseInteractionSupported(eventSpec)
    }

    companion object {
        private fun createCanvasFigure(layer: GeomLayer, bounds: DoubleRectangle): LiveMapProvider.LiveMapData {
            return (layer.geom as LiveMapGeom).createCanvasFigure(bounds)
        }

        private const val DEBUG_DRAWING = PLOT_DEBUG_DRAWING
    }

    inner class TransientState : ComponentTransientState(
        viewBounds = tileLayoutInfo.geomContentBounds
    ) {
        private val coreTransientState: ComponentTransientState = frameOfReference.transientState

        override val dataBounds: DoubleRectangle
            get() = coreTransientState.dataBounds
        override val isCoordFlip: Boolean
            get() = coreTransientState.isCoordFlip

        override fun syncDataBounds(ctx: InteractionContext) {
            coreTransientState.setScaleAndOffset(scale, offset)
            coreTransientState.syncDataBounds(ctx)
        }

        override fun repaint(ctx: InteractionContext) {
            val transform = SvgTransformBuilder()
                .scale(scale.x, scale.y)
                .translate(offset)
                .build()

            geomInteractionGroup.rootGroup.transform().set(transform)
            coreTransientState.repaint(ctx)
        }
    }
}
