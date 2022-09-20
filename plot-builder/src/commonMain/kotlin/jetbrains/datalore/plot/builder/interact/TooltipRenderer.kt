/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.WHITE
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.base.values.Colors.mimicTransparency
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker
import jetbrains.datalore.plot.builder.interact.loc.TransformedTargetLocator
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.BORDER_RADIUS
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DARK_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.TooltipsTheme
import jetbrains.datalore.plot.builder.tooltip.CrosshairComponent
import jetbrains.datalore.plot.builder.tooltip.RetainableComponents
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgGraphicsElement.Visibility
import jetbrains.datalore.vis.svg.SvgNode


internal class TooltipRenderer(
    decorationLayer: SvgNode,
    private val flippedAxis: Boolean,
    plotSize: DoubleVector,
    private val xAxisTheme: AxisTheme,
    private val yAxisTheme: AxisTheme,
    private val tooltipsTheme: TooltipsTheme,
    private val plotBackground: Color,
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

        val tooltipSpecs = createTooltipSpecs(tileInfo.findTargets(cursor), tileInfo.axisOrigin)
        val geomBounds = tileInfo.geomBounds
        val tooltipComponents = tooltipStorage.provide(tooltipSpecs.size)

        tooltipSpecs
            .filter { it.lines.isNotEmpty() }
            .zip(tooltipComponents)
            .map { (spec, tooltipBox) ->

                val fillColor = when {
                    spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipFill()
                    spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipFill()
                    spec.isOutlier -> (spec.fill ?: WHITE).let { mimicTransparency(it, it.alpha / 255.0, WHITE) }
                    else -> tooltipsTheme.tooltipFill()
                }

                val borderColor = when {
                    spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipColor()
                    spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipColor()
                    spec.isOutlier -> if (fillColor.isDark()) LIGHT_TEXT_COLOR else DARK_TEXT_COLOR
                    else -> tooltipsTheme.tooltipColor()
                }

                // Text color is set by element class name,
                // but for outliers the color is not constant - it depends on the fill color
                val textColor = when {
                    spec.layoutHint.kind !in listOf(X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP) && spec.isOutlier -> borderColor
                    else -> null
                }

                val strokeWidth = when {
                    spec.layoutHint.kind == X_AXIS_TOOLTIP -> xAxisTheme.tooltipStrokeWidth()
                    spec.layoutHint.kind == Y_AXIS_TOOLTIP -> yAxisTheme.tooltipStrokeWidth()
                    spec.isOutlier -> 1.0
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
            .run { myLayoutManager.arrange(tooltips = this, cursorCoord = cursor, geomBounds) }
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
        axisOrigin: DoubleVector
    ) {
        val tileInfo = TileInfo(
            geomBounds,
            targetLocators,
            layerYOrientations,
            flippedAxis,
            axisOrigin
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
        axisOrigin: DoubleVector
    ): List<TooltipSpec> {
        val tooltipSpecs = ArrayList<TooltipSpec>()

        lookupResults.forEach { result ->
            val factory = TooltipSpecFactory(result.contextualMapping, axisOrigin, flippedAxis, xAxisTheme, yAxisTheme)
            result.targets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget)) }
        }

        return tooltipSpecs
    }

    private class TileInfo(
        val geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        layerYOrientations: List<Boolean>,
        private val flippedAxis: Boolean,
        val axisOrigin: DoubleVector
    ) {

        private val transformedLocators = targetLocators.zip(layerYOrientations)
            .map { (targetLocator, isYOrientation) ->
                val flip = if (isYOrientation) !flippedAxis else flippedAxis
                when (flip) {
                    true -> FlippedTileTargetLocator(targetLocator)
                    false -> TileTargetLocator(targetLocator)
                }
            }

        internal fun findTargets(plotCoord: DoubleVector): List<GeomTargetLocator.LookupResult> {
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

        internal operator fun contains(plotCoord: DoubleVector) = geomBounds.contains(plotCoord)

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
