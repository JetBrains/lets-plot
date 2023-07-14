/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.builder.tooltip.spec.TooltipSpec
import jetbrains.datalore.plot.builder.tooltip.spec.TooltipSpecFactory
import jetbrains.datalore.plot.builder.tooltip.loc.LocatedTargetsPicker
import jetbrains.datalore.plot.builder.tooltip.loc.TransformedTargetLocator
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_RADIUS
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.tooltip.component.CrosshairComponent
import jetbrains.datalore.plot.builder.tooltip.component.RetainableComponents
import jetbrains.datalore.plot.builder.tooltip.component.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.component.TooltipBox.Orientation
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
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
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.TooltipsTheme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.Visibility
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode


internal class TooltipRenderer constructor(
    decorationLayer: SvgNode,
    private val flippedAxis: Boolean,
    plotSize: DoubleVector,
    private val xAxisTheme: AxisTheme,
    private val yAxisTheme: AxisTheme,
    private val tooltipsTheme: TooltipsTheme,
    private val plotBackground: Color,
    private val plotContext: PlotContext,
    mouseEventPeer: MouseEventPeer
) : Disposable {
    private val regs = CompositeRegistration()
    private val myLayoutManager: LayoutManager
    private val myTooltipLayer: SvgGElement
    private val myTileInfos = ArrayList<TileInfo>()
    private val tooltipStorage: RetainableComponents<TooltipBox>
    private val crosshairStorage: RetainableComponents<CrosshairComponent>

    init {
        val viewport = DoubleRectangle(DoubleVector.ZERO, plotSize)
        myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT)

        myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }
        crosshairStorage = RetainableComponents(
            itemFactory = ::CrosshairComponent,
            parent = SvgGElement().also { myTooltipLayer.children().add(it) }
        )
        tooltipStorage = RetainableComponents(
            itemFactory = ::TooltipBox,
            parent = SvgGElement().also { myTooltipLayer.children().add(it) }
        )

        regs.add(mouseEventPeer.addEventHandler(MOUSE_MOVED, handler { showTooltips(it.location.toDoubleVector()) }))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_DRAGGED, handler { hideTooltips() }))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_LEFT, handler { hideTooltips() }))
        regs.add(mouseEventPeer.addEventHandler(MOUSE_DOUBLE_CLICKED, handler { hideTooltips() }))
    }

    override fun dispose() {
        myTileInfos.clear()
        regs.dispose()
    }

    private fun showTooltips(cursor: DoubleVector) {
        val tileInfo = findTileInfo(cursor)
        if (tileInfo == null) {
            hideTooltips()
            return
        }

        val tooltipSpecs = createTooltipSpecs(tileInfo.findTargets(cursor), tileInfo.axisOrigin, plotContext)
        val geomBounds = tileInfo.geomBounds
        val tooltipComponents = tooltipStorage.provide(tooltipSpecs.size)

        tooltipSpecs
            .filter { it.lines.isNotEmpty() }
            .zip(tooltipComponents)
            .map { (spec, tooltipBox) ->

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

                val strokeWidth = when {
                    spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipStrokeWidth()
                    spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipStrokeWidth()
                    spec.isSide -> 1.0
                    else -> tooltipsTheme.tooltipStrokeWidth()
                }

                val borderRadius = when (spec.layoutHint.kind) {
                    X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP -> 0.0
                    else -> BORDER_RADIUS
                }

                tooltipBox
                    // not all tooltips will get position - overlapped axis toooltips likely won't.
                    // Hide and later show only ones with position
                    .apply { rootGroup.visibility().set(Visibility.HIDDEN) }
                    .update(
                        fillColor = fillColor,
                        textColor = textColor,
                        borderColor = borderColor,
                        strokeWidth = strokeWidth,
                        lines = spec.lines,
                        title = spec.title,
                        textClassName = spec.style,
                        rotate = spec.layoutHint.kind == ROTATED_TOOLTIP,
                        tooltipMinWidth = spec.minWidth,
                        borderRadius = borderRadius,
                        markerColors = spec.markerColors.distinct(),
                        pointMarkerStrokeColor = plotBackground
                    )
                MeasuredTooltip(tooltipSpec = spec, tooltipBox = tooltipBox, strokeWidth = strokeWidth)
            }
            .run {
                myLayoutManager.arrange(
                    tooltips = this,
                    cursorCoord = cursor,
                    geomBounds,
                    tileInfo.hAxisTooltipPosition,
                    tileInfo.vAxisTooltipPosition
                )
            }
            .also { tooltips -> showCrosshair(tooltips, geomBounds) }
            .forEach { arranged ->
                arranged.tooltipBox.apply {
                    rootGroup.visibility().set(Visibility.VISIBLE) // show only tooltips that got their position
                    setPosition(
                        arranged.tooltipCoord,
                        arranged.stemCoord,
                        arranged.orientation,
                        arranged.tooltipSpec.layoutHint.kind == ROTATED_TOOLTIP
                    )
                }
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

        val crosshariComponents = crosshairStorage.provide(coords.size)

        coords.zip(crosshariComponents)
            .forEach { (coord, crosshairComponent) ->
                crosshairComponent.update(
                    coord = coord,
                    geomBounds = geomBounds,
                    showHorizontal = showHorizontal,
                    showVertical = showVertical
                )
            }
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

    private fun createTooltipSpecs(
        lookupResults: List<GeomTargetLocator.LookupResult>,
        axisOrigin: DoubleVector,
        ctx: PlotContext
    ): List<TooltipSpec> {
        val tooltipSpecs = ArrayList<TooltipSpec>()

        lookupResults.forEach { result ->
            val factory = TooltipSpecFactory(result.contextualMapping, axisOrigin, flippedAxis, xAxisTheme, yAxisTheme)
            result.targets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget, ctx)) }
        }

        return tooltipSpecs
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
}
