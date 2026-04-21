/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.event.Button.LEFT
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Color.Companion.WHITE
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.Colors.mimicTransparency
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.TooltipsTheme
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.CrosshairComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.SvgComponentPool
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox
import org.jetbrains.letsPlot.core.plot.base.tooltip.component.TooltipBox.Orientation
import org.jetbrains.letsPlot.core.plot.base.tooltip.layout.LayoutManager
import org.jetbrains.letsPlot.core.plot.base.tooltip.layout.LayoutManager.HorizontalAlignment
import org.jetbrains.letsPlot.core.plot.base.tooltip.layout.LayoutManager.MeasuredTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.LocatedTargetsPicker
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.TransformedTargetLocator
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.Visibility
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet


class TooltipRenderer(
    decorationLayer: SvgNode,
    private val flippedAxis: Boolean,
    plotSize: DoubleVector,
    private val xAxisTheme: AxisTheme,
    private val yAxisTheme: AxisTheme,
    private val tooltipsTheme: TooltipsTheme,
    private val plotBackground: Color,
    private val styleSheet: StyleSheet,
    private val plotContext: PlotContext,
    mouseEventPeer: MouseEventPeer
) : Disposable {
    private val regs = CompositeRegistration()
    private val myLayoutManager: LayoutManager
    private val myTooltipLayer: SvgGElement
    private val measuringTooltipBox: TooltipBox
    private val myTileInfos = ArrayList<TileInfo>()
    private val tooltipStorage: SvgComponentPool<TooltipBox>
    private val crosshairStorage: SvgComponentPool<CrosshairComponent>
    private val fadeEffectRect: SvgRectElement
    private var pinned = false

    init {
        val viewport = DoubleRectangle(DoubleVector.ZERO, plotSize)
        myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT, TooltipDefaults.MARGIN_BETWEEN_TOOLTIPS)
        measuringTooltipBox = TooltipBox(styleSheet).apply {
            rootGroup.visibility().set(Visibility.HIDDEN)
        }

        myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }
        myTooltipLayer.children().add(measuringTooltipBox.rootGroup)

        crosshairStorage = SvgComponentPool(
            itemFactory = ::CrosshairComponent,
            parent = SvgGElement().also { myTooltipLayer.children().add(it) }
        )
        tooltipStorage = SvgComponentPool(
            itemFactory = { TooltipBox(styleSheet) },
            parent = SvgGElement().also { myTooltipLayer.children().add(it) }
        )

        fadeEffectRect = SvgRectElement().apply {
            width().set(0.0)
            height().set(0.0)
            fillColor().set(plotBackground.changeAlpha((255 * 0.7).toInt()))
            visibility().set(Visibility.HIDDEN)
            decorationLayer.children().add(0, this)
        }

        regs.add(mouseEventPeer.addEventHandler(MOUSE_MOVED, handler(::onMouseMoved)))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_DRAGGED, handler(::onMouseDragged)))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_LEFT, handler(::onMouseLeft)))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_CLICKED, handler(::onMouseClicked)))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_DOUBLE_CLICKED, handler(::onMouseDoubleClicked)))
    }

    override fun dispose() {
        myTileInfos.clear()
        regs.dispose()
    }

    private fun measureTooltip(tooltipModel: TooltipModel): MeasuredTooltip {
        applySpec(measuringTooltipBox, tooltipModel)
        return MeasuredTooltip(
            tooltipModel = tooltipModel,
            strokeWidth = getStrokeWidth(tooltipModel),
            size = measuringTooltipBox.contentRect.dimension
        )
    }

    private fun showTooltips(cursor: DoubleVector) {
        val tileInfo = findTileInfo(cursor)
        if (tileInfo == null) {
            hideTooltips()
            return
        }

        val lookupResults = tileInfo.findTargets(cursor)

        val measuredTooltips = lookupResults.map(::measureTooltip)

        val positionedTooltips = myLayoutManager.arrange(
            measuredTooltips,
            cursor,
            tileInfo.geomBounds,
            tileInfo.hAxisTooltipPosition,
            tileInfo.vAxisTooltipPosition
        )

        showCrosshair(positionedTooltips, tileInfo.geomBounds)

        tooltipStorage.provide(positionedTooltips.size)
            .zip(positionedTooltips)
            .forEach { (tooltipComponent, info) ->
                applySpec(tooltipComponent, info.tooltipModel)
                tooltipComponent.setPosition(
                    info.tooltipCoord,
                    info.stemCoord,
                    info.orientation,
                    info.tooltipModel.tooltipHint.placement == ROTATED
                )
            }
    }

    private fun hideTooltips() {
        tooltipStorage.provide(0)
        crosshairStorage.provide(0)
    }

    private fun showCrosshair(tooltips: List<LayoutManager.PositionedTooltip>, geomBounds: DoubleRectangle) {
        val showVertical = tooltips.any { it.hintKind == X_AXIS }
        val showHorizontal = tooltips.any { it.hintKind == Y_AXIS }
        if (!showVertical && !showHorizontal) {
            crosshairStorage.provide(0)
            return
        }
        val coords = tooltips
            .filter { tooltip -> tooltip.tooltipModel.isCrosshairEnabled }
            .map { tooltip -> tooltip.tooltipModel.tooltipHint.coord }
            .toList()

        val crosshairComponents = crosshairStorage.provide(coords.size)

        coords.zip(crosshairComponents)
            .forEach { (coord, crosshairComponent) ->
                crosshairComponent.update(
                    coord = coord,
                    geomBounds = geomBounds,
                    showHorizontal = showHorizontal,
                    showVertical = showVertical
                )
            }
    }

    private fun onMouseClicked(mouseEvent: MouseEvent) {
        if (mouseEvent.button != LEFT) return

        // On a Livemap, this allows zooming in with a tooltip, avoiding the epilepsy-inducing blinking fade effect
        if (mouseEvent.modifiers.isCtrl) return

        if (pinned) {
            unpin()
            hideTooltips()
        } else {
            if (tooltipStorage.size == 0) return
            val geomBounds = findTileInfo(mouseEvent.location.toDoubleVector())?.geomBounds ?: return
            pin(geomBounds)
        }
    }

    private fun onMouseDoubleClicked(@Suppress("UNUSED_PARAMETER") mouseEvent: MouseEvent) {
        // Double-clicking on the plot resets zoom and pan or zooms the livemap
        // so we should unpin the tooltip to remove the fade effect
        if (pinned) {
            unpin()
        }

        hideTooltips()
    }

    private fun onMouseLeft(@Suppress("UNUSED_PARAMETER") mouseEvent: MouseEvent) {
        // Not yet happened. Subj to change if needed.
        if (!pinned) {
            hideTooltips()
        }
    }

    private fun onMouseDragged(@Suppress("UNUSED_PARAMETER") mouseEvent: MouseEvent) {
        // Dragging the plot should unpin the tooltip to remove the fade effect and make the plot interactive again
        // This is also needed for the livemap for the same reason
        if (pinned) {
            unpin()
        }

        hideTooltips()
    }

    private fun onMouseMoved(mouseEvent: MouseEvent) {
        if (!pinned) {
            showTooltips(mouseEvent.location.toDoubleVector())
        }
    }

    private fun unpin() {
        fadeEffectRect.width().set(0.0)
        fadeEffectRect.height().set(0.0)
        fadeEffectRect.visibility().set(Visibility.HIDDEN)
        pinned = false
    }

    private fun pin(geomBounds: DoubleRectangle) {
        fadeEffectRect.x().set(geomBounds.left)
        fadeEffectRect.y().set(geomBounds.top)
        fadeEffectRect.width().set(geomBounds.width)
        fadeEffectRect.height().set(geomBounds.height)
        fadeEffectRect.visibility().set(Visibility.VISIBLE)
        pinned = true
    }

    fun addTileInfo(
        geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        layerYOrientations: List<Boolean>,
        axisOrigin: DoubleVector,
        hAxisTooltipPosition: HorizontalAxisTooltipPosition,
        vAxisTooltipPosition: VerticalAxisTooltipPosition
    ) {
        val tileInfo = TileInfo(
            geomBounds,
            targetLocators,
            layerYOrientations,
            flippedAxis,
            axisOrigin,
            xAxisTheme,
            yAxisTheme,
            plotContext,
            hAxisTooltipPosition,
            vAxisTooltipPosition
        )
        myTileInfos.add(tileInfo)
    }

    private fun findTileInfo(plotCoord: DoubleVector): TileInfo? {
        for (tileInfo in myTileInfos) {
            if (tileInfo.contains(plotCoord)) {
                return tileInfo
            }
        }
        return null
    }

    private class TileInfo(
        val geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        layerYOrientations: List<Boolean>,
        private val flippedAxis: Boolean,
        val axisOrigin: DoubleVector,
        private val xAxisTheme: AxisTheme,
        private val yAxisTheme: AxisTheme,
        private val plotContext: PlotContext,
        val hAxisTooltipPosition: HorizontalAxisTooltipPosition,
        val vAxisTooltipPosition: VerticalAxisTooltipPosition
    ) {

        private val transformedLocators = targetLocators.zip(layerYOrientations)
            .map { (targetLocator, isYOrientation) ->
                val flip = if (isYOrientation) !flippedAxis else flippedAxis
                when (flip) {
                    true -> FlippedTileTargetLocator(targetLocator)
                    false -> TileTargetLocator(targetLocator)
                }
            }

        fun findTargets(plotCoord: DoubleVector): List<TooltipModel> {
            val targetsPicker = LocatedTargetsPicker(
                flippedAxis = flippedAxis,
                cursorCoord = plotCoord,
                axisOrigin = axisOrigin,
                xAxisTheme = xAxisTheme,
                yAxisTheme = yAxisTheme,
                ctx = plotContext
            ).apply {
                for (locator in transformedLocators) {
                    val result = locator.search(plotCoord)
                    if (result != null) {
                        addLookupResult(result)
                    }
                }
            }
            return targetsPicker.chooseBestResult()
        }

        operator fun contains(plotCoord: DoubleVector) = geomBounds.contains(plotCoord)

        private inner class TileTargetLocator(
            locator: GeomTargetLocator
        ) : TransformedTargetLocator(locator) {
            override fun convertToTargetCoord(coord: DoubleVector) = coord.subtract(geomBounds.origin)
            override fun convertToPlotCoord(coord: DoubleVector) = coord.add(geomBounds.origin)
            override fun convertToPlotDistance(distance: Double) = distance
        }

        private inner class FlippedTileTargetLocator(
            locator: GeomTargetLocator
        ) : TransformedTargetLocator(locator) {
            override fun convertToTargetCoord(coord: DoubleVector) = coord.subtract(geomBounds.origin).flip()
            override fun convertToPlotCoord(coord: DoubleVector) = coord.flip().add(geomBounds.origin)
            override fun convertToPlotDistance(distance: Double) = distance
        }
    }

    private fun Color.isDark() = Colors.luminance(this) < 0.5

    private val TooltipModel.style
        get() =
            when (tooltipHint.placement) {
                X_AXIS -> "${TooltipStyle.AXIS_TOOLTIP_TEXT}-${xAxisTheme.axis}"
                Y_AXIS -> "${TooltipStyle.AXIS_TOOLTIP_TEXT}-${yAxisTheme.axis}"
                VERTICAL -> TooltipStyle.TOOLTIP_TEXT
                HORIZONTAL -> TooltipStyle.TOOLTIP_TEXT
                CURSOR -> TooltipStyle.TOOLTIP_TEXT
                ROTATED -> TooltipStyle.TOOLTIP_TEXT
            }

    private val LayoutManager.PositionedTooltip.orientation
        get() =
            when (hintKind) {
                HORIZONTAL -> Orientation.HORIZONTAL
                Y_AXIS -> Orientation.HORIZONTAL
                VERTICAL -> Orientation.VERTICAL
                CURSOR -> Orientation.VERTICAL
                X_AXIS -> Orientation.VERTICAL
                ROTATED -> Orientation.VERTICAL
            }

    private fun applySpec(tooltipBox: TooltipBox, spec: TooltipModel) {
        val fillColor = when {
            spec.tooltipHint.placement == X_AXIS -> xAxisTheme.tooltipFill()
            spec.tooltipHint.placement == Y_AXIS -> yAxisTheme.tooltipFill()
            spec.isSide -> (spec.fill ?: WHITE).let { mimicTransparency(it, it.alpha / 255.0, WHITE) }
            else -> tooltipsTheme.tooltipFill()
        }

        val borderColor = when {
            spec.tooltipHint.placement == X_AXIS -> xAxisTheme.tooltipColor()
            spec.tooltipHint.placement == Y_AXIS -> yAxisTheme.tooltipColor()
            spec.isSide -> if (fillColor.isDark()) TooltipDefaults.LIGHT_TEXT_COLOR else TooltipDefaults.DARK_TEXT_COLOR
            else -> tooltipsTheme.tooltipColor()
        }

        // Text color is set by element class name,
        // but for side tooltips the color is not constant - it depends on the fill color
        val textColor = when {
            spec.tooltipHint.placement !in listOf(X_AXIS, Y_AXIS) && spec.isSide -> borderColor
            else -> null
        }

        val strokeWidth = getStrokeWidth(spec)

        val lineType = when {
            spec.tooltipHint.placement == X_AXIS -> xAxisTheme.tooltipLineType()
            spec.tooltipHint.placement == Y_AXIS -> yAxisTheme.tooltipLineType()
            spec.isSide -> NamedLineType.SOLID
            else -> tooltipsTheme.tooltipLineType()
        }

        val borderRadius = when (spec.tooltipHint.placement) {
            X_AXIS, Y_AXIS -> 0.0
            else -> TooltipDefaults.BORDER_RADIUS
        }

        tooltipBox
            .update(
                fillColor = fillColor,
                textColor = textColor,
                borderColor = borderColor,
                strokeWidth = strokeWidth,
                lineType = lineType,
                lines = spec.lines,
                title = spec.title,
                textClassName = spec.style,
                tooltipMinWidth = spec.minWidth,
                borderRadius = borderRadius,
                markerColors = spec.markerColors.distinct(),
                pointMarkerStrokeColor = plotBackground
            )
    }

    private fun getStrokeWidth(spec: TooltipModel): Double = when {
        spec.tooltipHint.placement == X_AXIS -> xAxisTheme.tooltipStrokeWidth()
        spec.tooltipHint.placement == Y_AXIS -> yAxisTheme.tooltipStrokeWidth()
        spec.isSide -> 1.0
        else -> tooltipsTheme.tooltipStrokeWidth()
    }
}
