/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker
import jetbrains.datalore.plot.builder.interact.loc.TransformedTargetLocator
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.tooltip.CrosshairComponent
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.TooltipBox.Orientation
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgNode


internal class TooltipRenderer(
    decorationLayer: SvgNode,
    private val flippedAxis: Boolean,
    plotSize: DoubleVector,
    mouseEventPeer: MouseEventPeer
) : Disposable {
    private val regs = CompositeRegistration()
    private val myLayoutManager: LayoutManager
    private val myTooltipLayer: SvgGElement
    private val myTileInfos = ArrayList<TileInfo>()

    init {
        val viewport = DoubleRectangle(DoubleVector.ZERO, plotSize)
        myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT)
        myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }

        regs.add(mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_MOVED, handler(this::onMouseMove)))
        regs.add(mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_DRAGGED, handler(this::onMouseDrag)))
        regs.add(mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_LEFT, handler(this::onMouseLeft)))
    }

    override fun dispose() {
        myTileInfos.clear()
        regs.dispose()
    }

    private fun onMouseMove(e: MouseEvent) {
        val coord = DoubleVector(e.x.toDouble(), e.y.toDouble())

        showTooltips(coord)
    }

    private fun onMouseDrag(e: MouseEvent) {
        hideTooltip()
    }

    private fun onMouseLeft(e: MouseEvent) {
        hideTooltip()
    }

    private fun showTooltips(cursor: DoubleVector) {
        val tooltipSpecs = createTooltipSpecs(cursor)
        val tooltipBounds = getTooltipBounds(cursor)

        clearTooltips()

        tooltipSpecs
            .filter { spec -> spec.lines.isNotEmpty() }
            .map { spec ->
                spec
                    .run { newTooltipBox(spec.minWidth).apply { visible = false } } // to not flicker on arrange
                    .apply { setContent(spec.fill, spec.lines, spec.style, spec.isOutlier, spec.layoutHint.kind == ROTATED_TOOLTIP) }
                    .run { MeasuredTooltip(tooltipSpec = spec, tooltipBox = this) }
            }
            .run { myLayoutManager.arrange(tooltips = this, cursorCoord = cursor, tooltipBounds) }
            .also { tooltips -> tooltipBounds?.let { showCrosshair(tooltips, it.handlingArea) } }
            .map { arranged ->
                arranged.tooltipBox.apply {
                    setPosition(
                        arranged.tooltipCoord,
                        arranged.stemCoord,
                        arranged.orientation
                    )
                }
            }.forEach { it.visible = true } // arranged, show tooltips
    }

    private fun hideTooltip() = clearTooltips()

    private fun clearTooltips() = myTooltipLayer.children().clear()

    private fun newTooltipBox(tooltipMinWidth: Double?): TooltipBox {
        // Add to the layer to be able to calculate a bbox
        return TooltipBox(tooltipMinWidth).apply { myTooltipLayer.children().add(rootGroup) }
    }

    private fun newCrosshairComponent(): CrosshairComponent {
        return CrosshairComponent().apply { myTooltipLayer.children().add(0, rootGroup) }
    }

    private fun showCrosshair(tooltips: List<LayoutManager.PositionedTooltip>, geomBounds: DoubleRectangle) {
        val showVertical = tooltips.any { it.hintKind == X_AXIS_TOOLTIP }
        val showHorizontal = tooltips.any { it.hintKind == Y_AXIS_TOOLTIP }
        if (!showVertical && !showHorizontal) {
            return
        }
        tooltips
            .filter { tooltip -> tooltip.tooltipSpec.isCrosshairEnabled }
            .mapNotNull { tooltip -> tooltip.tooltipSpec.layoutHint.coord }
            .forEach { coord ->
                newCrosshairComponent().also { crosshair ->
                    if (showHorizontal) crosshair.addHorizontal(coord, geomBounds)
                    if (showVertical) crosshair.addVertical(coord, geomBounds)
                }
            }
    }


    fun addTileInfo(
        geomBounds: DoubleRectangle,
        tooltipBounds: PlotTooltipBounds,
        targetLocators: List<GeomTargetLocator>
    ) {
        val tileInfo = TileInfo(
            geomBounds,
            tooltipBounds,
            targetLocators,
            flippedAxis
        )
        myTileInfos.add(tileInfo)
    }

    private fun createTooltipSpecs(plotCoord: DoubleVector): List<TooltipSpec> {
        val tileInfo = findTileInfo(plotCoord) ?: return emptyList()

        val lookupResults = tileInfo.findTargets(plotCoord)
        return createTooltipSpecs(lookupResults, tileInfo.axisOrigin)
    }

    private fun getTooltipBounds(plotCoord: DoubleVector): PlotTooltipBounds? {
        val tileInfo = findTileInfo(plotCoord) ?: return null
        return tileInfo.tooltipBounds
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
            val factory = TooltipSpecFactory(result.contextualMapping, axisOrigin)
            result.targets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget, flippedAxis)) }
        }

        return tooltipSpecs
    }

    private class TileInfo(
        val geomBounds: DoubleRectangle,
        val tooltipBounds: PlotTooltipBounds,
        targetLocators: List<GeomTargetLocator>,
        private val flippedAxis: Boolean
    ) {

        private val myTargetLocators = targetLocators.map {
            if (flippedAxis) FlippedTileTargetLocator(it) else TileTargetLocator(it)
        }

        internal val axisOrigin: DoubleVector
            get() = DoubleVector(geomBounds.left, geomBounds.bottom)

        internal fun findTargets(plotCoord: DoubleVector): List<GeomTargetLocator.LookupResult> {
            val targetsPicker = LocatedTargetsPicker().apply {
                for (locator in myTargetLocators) {
                    val result = locator.search(plotCoord)
                    if (result != null) {
                        addLookupResult(result, plotCoord, flippedAxis)
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

    private val TooltipSpec.style
        get() =
            when (layoutHint.kind) {
                X_AXIS_TOOLTIP -> Style.PLOT_AXIS_TOOLTIP
                Y_AXIS_TOOLTIP -> Style.PLOT_AXIS_TOOLTIP
                VERTICAL_TOOLTIP -> Style.PLOT_DATA_TOOLTIP
                HORIZONTAL_TOOLTIP -> Style.PLOT_DATA_TOOLTIP
                CURSOR_TOOLTIP -> Style.PLOT_DATA_TOOLTIP
                ROTATED_TOOLTIP -> Style.PLOT_DATA_TOOLTIP
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
