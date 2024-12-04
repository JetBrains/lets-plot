/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

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
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.*
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_RADIUS
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.tooltip.component.CrosshairComponent
import org.jetbrains.letsPlot.core.plot.builder.tooltip.component.RetainableComponents
import org.jetbrains.letsPlot.core.plot.builder.tooltip.component.TooltipBox
import org.jetbrains.letsPlot.core.plot.builder.tooltip.component.TooltipBox.Orientation
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.LocatedTargetsPicker
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.TransformedTargetLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpecFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.Visibility
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet


internal class TooltipRenderer(
    decorationLayer: SvgNode,
    private val flippedAxis: Boolean,
    private val plotSize: DoubleVector,
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
    private val tooltipStorage: RetainableComponents<TooltipBox>
    private val crosshairStorage: RetainableComponents<CrosshairComponent>
    private val fadeEffectRect: SvgRectElement
    private var pinned = false

    init {
        val viewport = DoubleRectangle(DoubleVector.ZERO, plotSize)
        myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT)
        measuringTooltipBox = TooltipBox(styleSheet).apply {
            rootGroup.visibility().set(Visibility.HIDDEN)
        }

        myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }
        myTooltipLayer.children().add(measuringTooltipBox.rootGroup)

        crosshairStorage = RetainableComponents(
            itemFactory = ::CrosshairComponent,
            parent = SvgGElement().also { myTooltipLayer.children().add(it) }
        )
        tooltipStorage = RetainableComponents(
            itemFactory = { TooltipBox(styleSheet) },
            parent = SvgGElement().also { myTooltipLayer.children().add(it) }
        )

        fadeEffectRect = SvgRectElement().apply {
            width().set(0.0)
            height().set(0.0)
            fillColor().set(plotBackground)
            opacity().set(0.7)
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

    private fun measureTooltip(tooltipSpec: TooltipSpec): MeasuredTooltip {
        applySpec(measuringTooltipBox, tooltipSpec)
        return MeasuredTooltip(
            tooltipSpec = tooltipSpec,
            strokeWidth = getStrokeWidth(tooltipSpec),
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

        val tooltips = lookupResults
            .flatMap { tooltipSpecFromLookupResult(it, tileInfo.axisOrigin) }
            .filter { it.lines.isNotEmpty() }

        val measuredTooltips = tooltips.map(::measureTooltip)

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
                applySpec(tooltipComponent, info.tooltipSpec)
                tooltipComponent.setPosition(
                    info.tooltipCoord,
                    info.stemCoord,
                    info.orientation,
                    info.tooltipSpec.layoutHint.kind == ROTATED_TOOLTIP
                )
            }
    }

    private fun hideTooltips() {
        tooltipStorage.provide(0)
        crosshairStorage.provide(0)
    }

    private fun showCrosshair(tooltips: List<LayoutManager.PositionedTooltip>, geomBounds: DoubleRectangle) {
        val showVertical = tooltips.any { it.hintKind == X_AXIS_TOOLTIP }
        val showHorizontal = tooltips.any { it.hintKind == Y_AXIS_TOOLTIP }
        if (!showVertical && !showHorizontal) {
            crosshairStorage.provide(0)
            return
        }
        val coords = tooltips
            .filter { tooltip -> tooltip.tooltipSpec.isCrosshairEnabled }
            .mapNotNull { tooltip -> tooltip.tooltipSpec.layoutHint.coord }
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

    private fun onMouseDoubleClicked(mouseEvent: MouseEvent) {
        // Double-clicking on the plot resets zoom and pan or zooms the livemap
        // so we should unpin the tooltip to remove the fade effect
        if (pinned) {
            unpin()
        }

        hideTooltips()
    }

    private fun onMouseLeft(mouseEvent: MouseEvent) {
        // Not yet happened. Subj to change if needed.
        if (!pinned) {
            hideTooltips()
        }
    }

    private fun onMouseDragged(mouseEvent: MouseEvent) {
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
        pinned = false
    }

    private fun pin(geomBounds: DoubleRectangle) {
        fadeEffectRect.x().set(geomBounds.left)
        fadeEffectRect.y().set(geomBounds.top)
        fadeEffectRect.width().set(geomBounds.width)
        fadeEffectRect.height().set(geomBounds.height)
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

        fun findTargets(plotCoord: DoubleVector): List<GeomTargetLocator.LookupResult> {
            val targetsPicker = LocatedTargetsPicker(flippedAxis, plotCoord).apply {
                for (locator in transformedLocators) {
                    val result = locator.search(plotCoord)
                    if (result != null) {
                        addLookupResult(result)
                    }
                }
            }
            return targetsPicker.picked
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

    private val TooltipSpec.style
        get() =
            when (layoutHint.kind) {
                X_AXIS_TOOLTIP -> "${Style.AXIS_TOOLTIP_TEXT}-${xAxisTheme.axis}"
                Y_AXIS_TOOLTIP -> "${Style.AXIS_TOOLTIP_TEXT}-${yAxisTheme.axis}"
                VERTICAL_TOOLTIP -> Style.TOOLTIP_TEXT
                HORIZONTAL_TOOLTIP -> Style.TOOLTIP_TEXT
                CURSOR_TOOLTIP -> Style.TOOLTIP_TEXT
                ROTATED_TOOLTIP -> Style.TOOLTIP_TEXT
            }

    private val LayoutManager.PositionedTooltip.orientation
        get() =
            when (hintKind) {
                HORIZONTAL_TOOLTIP -> Orientation.HORIZONTAL
                Y_AXIS_TOOLTIP -> Orientation.HORIZONTAL
                VERTICAL_TOOLTIP -> Orientation.VERTICAL
                CURSOR_TOOLTIP -> Orientation.VERTICAL
                X_AXIS_TOOLTIP -> Orientation.VERTICAL
                ROTATED_TOOLTIP -> Orientation.VERTICAL
            }

    private fun applySpec(tooltipBox: TooltipBox, spec: TooltipSpec) {
        val fillColor = when {
            spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipFill()
            spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipFill()
            spec.isSide -> (spec.fill ?: WHITE).let { mimicTransparency(it, it.alpha / 255.0, WHITE) }
            else -> tooltipsTheme.tooltipFill()
        }

        val borderColor = when {
            spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipColor()
            spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipColor()
            spec.isSide -> if (fillColor.isDark()) LIGHT_TEXT_COLOR else DARK_TEXT_COLOR
            else -> tooltipsTheme.tooltipColor()
        }

        // Text color is set by element class name,
        // but for side tooltips the color is not constant - it depends on the fill color
        val textColor = when {
            spec.layoutHint.kind !in listOf(X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP) && spec.isSide -> borderColor
            else -> null
        }

        val strokeWidth = getStrokeWidth(spec)

        val lineType = when {
            spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipLineType()
            spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipLineType()
            spec.isSide -> NamedLineType.SOLID
            else -> tooltipsTheme.tooltipLineType()
        }

        val borderRadius = when (spec.layoutHint.kind) {
            X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP -> 0.0
            else -> BORDER_RADIUS
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
                rotate = spec.layoutHint.kind == ROTATED_TOOLTIP,
                tooltipMinWidth = spec.minWidth,
                borderRadius = borderRadius,
                markerColors = spec.markerColors.distinct(),
                pointMarkerStrokeColor = plotBackground
            )
    }

    private fun getStrokeWidth(spec: TooltipSpec): Double = when {
        spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipStrokeWidth()
        spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipStrokeWidth()
        spec.isSide -> 1.0
        else -> tooltipsTheme.tooltipStrokeWidth()
    }

    private fun tooltipSpecFromLookupResult(
        lookupResult: GeomTargetLocator.LookupResult,
        axisOrigin: DoubleVector,
    ): List<TooltipSpec> {
        return TooltipSpecFactory(
            lookupResult.contextualMapping,
            axisOrigin,
            flippedAxis,
            xAxisTheme,
            yAxisTheme,
            plotContext
        ).let { lookupResult.targets.flatMap(it::create) }
    }
}
