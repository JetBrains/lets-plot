/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.base.gcommon.base.Throwables
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.FeatureSwitch.PLOT_DEBUG_DRAWING
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.base.render.svg.Text.HorizontalAnchor
import jetbrains.datalore.plot.base.render.svg.Text.VerticalAnchor
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.interact.PlotInteractor
import jetbrains.datalore.plot.builder.interact.PlotTooltipBounds
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.addTitlesAndLegends
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.axisTitleSizeDelta
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.legendBlockLeftTopDelta
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.liveMapBounds
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.subtractTitlesAndLegends
import jetbrains.datalore.plot.builder.presentation.Defaults.DEF_PLOT_SIZE
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.event.SvgEventHandler
import jetbrains.datalore.vis.svg.event.SvgEventSpec
import kotlin.math.max

class PlotSvgComponent constructor(
    title: String?,
    subtitle: String?,
    private val layersByTile: List<List<GeomLayer>>,
    private var plotLayout: PlotLayout,
    private val frameOfReferenceProviderByTile: List<TileFrameOfReferenceProvider>,
    private val coordProvider: CoordProvider,
    private val legendBoxInfos: List<LegendBoxInfo>,
    val interactionsEnabled: Boolean,
    val theme: Theme,
    caption: String?
) : SvgComponent() {

    private fun splitToLines(text: String?) = text?.split('\n')?.map(String::trim) ?: emptyList()

    private val titleLines: List<String> = splitToLines(title)
    private val subtitleLines: List<String> = splitToLines(subtitle)
    private val captionLines: List<String> = splitToLines(caption)

    val flippedAxis = frameOfReferenceProviderByTile[0].flipAxis
    val mouseEventPeer = MouseEventPeer()

    var interactor: PlotInteractor? = null
        set(value) {
            check(field == null) { "Can be intialize only once." }
            field = value
        }

    internal var liveMapFigures: List<SomeFig> = emptyList()
        private set

    var plotSize: DoubleVector = DEF_PLOT_SIZE
        private set

    // ToDo: remove
    private val axisTitleLeft: String? = frameOfReferenceProviderByTile[0].vAxisLabel

    // ToDo: remove
    private val axisTitleBottom: String? = frameOfReferenceProviderByTile[0].hAxisLabel

    private val containsLiveMap: Boolean = layersByTile.flatten().any(GeomLayer::isLiveMap)

    private fun tileLayers(tileIndex: Int): List<GeomLayer> {
        return layersByTile[tileIndex]
    }

    override fun buildComponent() {
        try {
            buildPlot()
        } catch (e: RuntimeException) {
            LOG.error(e) { "buildPlot" }

            val rootCause = Throwables.getRootCause(e)
            val messages = arrayOf(
                "Error building plot: " + rootCause::class.simpleName, if (rootCause.message != null)
                    "'" + rootCause.message + "'"
                else
                    "<no message>"
            )
            var y = plotSize.y / 2 - 8
            for (s in messages) {
                val errorLabel = TextLabel(s)
                errorLabel.setHorizontalAnchor(HorizontalAnchor.MIDDLE)
                errorLabel.setVerticalAnchor(VerticalAnchor.CENTER)
                errorLabel.moveTo(plotSize.x / 2, y)
                rootGroup.children().add(errorLabel.rootGroup)
                y += 16.0
            }
        }
    }

    private fun buildPlot() {
        rootGroup.addClass(Style.PLOT)
        buildPlotComponents()

        reg(object : Registration() {
            override fun doRemove() {
                interactor?.dispose()
                liveMapFigures = emptyList()
            }
        })
    }

    fun resize(plotSize: DoubleVector) {
        if (plotSize.x <= 0 || plotSize.y <= 0) return
        if (plotSize == this.plotSize) return

        this.plotSize = plotSize

        // just invalidate
        clear()
    }

    private fun buildPlotComponents() {
        val overallRect = DoubleRectangle(DoubleVector.ZERO, plotSize)

        val plotTheme = theme.plot()
        if (plotTheme.showBackground()) {
            add(SvgRectElement(overallRect).apply {
                strokeColor().set(plotTheme.backgroundColor())
                strokeWidth().set(plotTheme.backgroundStrokeWidth())
                fillColor().set(plotTheme.backgroundFill())
                if (containsLiveMap) {
                    // Don't fill rect over livemap figure.
                    fillOpacity().set(0.0)
                } else {
                    if (Colors.solid(plotTheme.backgroundFill())) {
                        // Workaround for JFX: 100% opaque rect blocks 'mouse left' events.
                        fillOpacity().set(0.99)
                    }
                }
            })
        }

        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            drawDebugRect(overallRect, Color.MAGENTA, "MAGENTA: overallRect")
        }

        // compute geom bounds
        val entirePlot = if (containsLiveMap) {
            liveMapBounds(overallRect)
        } else {
            overallRect
        }

        val legendTheme = theme.legend()
        val legendsBlockInfo = LegendBoxesLayoutUtil.arrangeLegendBoxes(
            legendBoxInfos,
            legendTheme
        )

        // -------------
        val axisEnabled = !containsLiveMap
        val plotInnerSizeAvailable = subtractTitlesAndLegends(
            baseSize = entirePlot.dimension,
            titleLines,
            subtitleLines,
            axisTitleLeft,
            axisTitleBottom,
            axisEnabled,
            legendsBlockInfo,
            theme,
            captionLines
        )

        // Layout plot inners
        val plotInfo = plotLayout.doLayout(plotInnerSizeAvailable, coordProvider)
        if (plotInfo.tiles.isEmpty()) {
            return
        }

        // Inner size includes geoms, axis and facet labels.
        val plotInnerSize = plotInfo.size
        val plotOuterSize = addTitlesAndLegends(
            plotInnerSize,
            titleLines,
            subtitleLines,
            axisTitleLeft,
            axisTitleBottom,
            axisEnabled,
            legendsBlockInfo,
            theme,
            captionLines
        )

        // Position the "entire" plot rect in the center of the "overall" rect.
        val plotOuterBounds = let {
            val delta = overallRect.center.subtract(
                DoubleRectangle(overallRect.origin, plotOuterSize).center
            )
            val deltaApplied = DoubleVector(max(0.0, delta.x), max(0.0, delta.y))
            val plotOuterOrigin = overallRect.origin.add(deltaApplied)
            DoubleRectangle(plotOuterOrigin, plotOuterSize)
        }
        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            drawDebugRect(plotOuterBounds, Color.BLUE, "BLUE: plotOuterBounds")
        }

        val plotOuterBoundsWithoutTitle = let {
            val titleSizeDelta = PlotLayoutUtil.titleSizeDelta(titleLines, subtitleLines)
            DoubleRectangle(
                plotOuterBounds.origin.add(titleSizeDelta),
                plotOuterBounds.dimension.subtract(titleSizeDelta)
            )
        }
        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            drawDebugRect(plotOuterBoundsWithoutTitle, Color.BLUE, "BLUE: plotOuterBoundsWithoutTitle")
        }

        // Inner bounds - all without titles and legends.
        val plotInnerOrigin = plotOuterBoundsWithoutTitle.origin
            .add(legendBlockLeftTopDelta(legendsBlockInfo, legendTheme))
            .add(axisTitleSizeDelta(axisTitleLeft, null, axisEnabled))

        val geomAreaBounds = PlotLayoutUtil.overallGeomBounds(plotInfo)
            .add(plotInnerOrigin)

        // build tiles
        @Suppress("UnnecessaryVariable")
        val tilesOrigin = plotInnerOrigin
        for (tileLayoutInfo in plotInfo.tiles) {
            val tileIndex = tileLayoutInfo.trueIndex

//            println("plot offset: " + tileInfo.plotOffset)
//            println("     bounds: " + tileInfo.bounds)
//            println("geom bounds: " + tileInfo.geomBounds)
//            println("clip bounds: " + tileInfo.clipBounds)

            // Create a plot tile.
            val frameOfReference = frameOfReferenceProviderByTile[tileIndex].createFrameOfReference(
                tileLayoutInfo,
                coordProvider,
                DEBUG_DRAWING
            )
            val tileLayers = tileLayers(tileIndex)
            val tile = PlotTile(tileLayers, tilesOrigin, tileLayoutInfo, theme, frameOfReference)

            val plotOriginAbsolute = tilesOrigin.add(tileLayoutInfo.plotOrigin)
            tile.moveTo(plotOriginAbsolute)

            add(tile)

            tile.liveMapFigure?.run {
                liveMapFigures = liveMapFigures + listOf(this)
            }

            val geomBoundsAbsolute = tileLayoutInfo.geomBounds.add(plotOriginAbsolute)
            val tooltipBounds = PlotTooltipBounds(
                placementArea = geomBoundsAbsolute,
                handlingArea = tile.geomDrawingBounds.add(geomBoundsAbsolute.origin)
            )
            interactor?.onTileAdded(geomBoundsAbsolute, tooltipBounds, tile.targetLocators)

            @Suppress("ConstantConditionIf")
            if (DEBUG_DRAWING) {
                drawDebugRect(tooltipBounds.handlingArea, Color.ORANGE, "ORANGE: tooltipBounds.handlingArea")
            }
        }

        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            drawDebugRect(geomAreaBounds, Color.RED, "RED: geomAreaBounds")
        }

        // add plot title
        if (titleLines.isNotEmpty()) {
            addTitle(
                titleLines,
                leftTop = DoubleVector(geomAreaBounds.left, plotOuterBounds.top),
                className = Style.PLOT_TITLE,
                labelSpec = PlotLabelSpec.PLOT_TITLE,
                color = theme.plot().titleColor()
            )
        }
        // add plot subtitle
        if (subtitleLines.isNotEmpty()) {
            val titleSize = PlotLayoutUtil.titleDimensions(titleLines, PlotLabelSpec.PLOT_TITLE)
            addTitle(
                subtitleLines,
                leftTop = DoubleVector(geomAreaBounds.left, plotOuterBounds.top + titleSize.y),
                className = Style.PLOT_SUBTITLE,
                labelSpec = PlotLabelSpec.PLOT_SUBTITLE,
                color = theme.plot().subtitleColor()
            )
        }

        val overallTileBounds = PlotLayoutUtil.overallTileBounds(plotInfo)
            .add(plotInnerOrigin)

        @Suppress("ConstantConditionIf")
        if (DEBUG_DRAWING) {
            drawDebugRect(overallTileBounds, Color.DARK_MAGENTA, "DARK_MAGENTA: overallTileBounds")
        }

        // add axis titles
        if (axisEnabled) {
            if (axisTitleLeft != null) {
                addAxisTitle(
                    axisTitleLeft,
                    Orientation.LEFT,
                    overallTileBounds,
                    geomAreaBounds,
                    theme.axisY(flippedAxis)
                )
            }
            if (axisTitleBottom != null) {
                addAxisTitle(
                    axisTitleBottom,
                    Orientation.BOTTOM,
                    overallTileBounds,
                    geomAreaBounds,
                    theme.axisX(flippedAxis)
                )
            }
        }

        // add legends
        if (!legendTheme.position().isHidden) {
            val legendsBlockInfoLayouted = LegendBoxesLayout(
                outerBounds = plotOuterBoundsWithoutTitle,
                innerBounds = geomAreaBounds,
                legendTheme
            ).doLayout(legendsBlockInfo)

            for (boxWithLocation in legendsBlockInfoLayouted.boxWithLocationList) {
                val legendBox = boxWithLocation.legendBox.createLegendBox()
                legendBox.moveTo(boxWithLocation.location)
                add(legendBox)
            }
        }

        // add caption
        if (captionLines.isNotEmpty()) {
            val captionLineHeight = PlotLabelSpec.PLOT_CAPTION.height()
            val captionLabel = MultilineLabel(captionLines.joinToString("\n"))
            captionLabel.addClassName(Style.PLOT_CAPTION)
            captionLabel.textColor().set(theme.plot().captionColor())
            captionLabel.setHorizontalAnchor(HorizontalAnchor.RIGHT)
            captionLabel.setX(0.0)
            captionLabel.setLineHeight(captionLineHeight)

            val captionSize = PlotLayoutUtil.titleDimensions(captionLines, PlotLabelSpec.PLOT_CAPTION)
            val captionBounds = DoubleRectangle(
                geomAreaBounds.right - captionSize.x,
                plotOuterBounds.bottom - captionSize.y,
                captionSize.x,
                captionSize.y
            )
            captionLabel.moveTo(captionBounds.right, captionBounds.top + captionLineHeight)
            add(captionLabel)

            if (DEBUG_DRAWING) {
                drawDebugRect(captionBounds, Color.BLUE)
            }
        }
    }

    private fun addTitle(
        titleLines: List<String>,
        leftTop: DoubleVector,
        className: String,
        labelSpec: LabelSpec,
        color: Color
    ) {
        val titleLineHeight = labelSpec.height()
        val titleLabel = MultilineLabel(titleLines.joinToString("\n"))
        titleLabel.addClassName(className)
        titleLabel.textColor().set(color)
        titleLabel.setHorizontalAnchor(HorizontalAnchor.LEFT)
        titleLabel.setX(0.0)
        titleLabel.setLineHeight(titleLineHeight)

        val titleSize = PlotLayoutUtil.titleDimensions(titleLines, labelSpec)
        val titleBounds = DoubleRectangle(leftTop, titleSize)

        titleLabel.moveTo(titleBounds.left, titleBounds.top + titleLineHeight)
        add(titleLabel)

        if (DEBUG_DRAWING) {
            var h = 0.0
            PlotLayoutUtil.textLinesDimensions(titleLines, labelSpec).forEach { sz ->
                val bounds = DoubleRectangle(
                    leftTop.add(DoubleVector(0.0, h)),
                    sz
                )
                h += sz.y
                drawDebugRect(bounds, Color.LIGHT_BLUE)
            }
            drawDebugRect(titleBounds, Color.BLUE)
        }
    }

    private fun addAxisTitle(
        text: String,
        orientation: Orientation,
        overallTileBounds: DoubleRectangle,  // tiles union bounds
        overallGeomBounds: DoubleRectangle,  // geom bounds union
        axisTheme: AxisTheme
    ) {
        val referenceRect = when (orientation) {
            Orientation.LEFT,
            Orientation.RIGHT ->
                DoubleRectangle(
                    overallTileBounds.left, overallGeomBounds.top,
                    overallTileBounds.width, overallGeomBounds.height
                )
            Orientation.TOP,
            Orientation.BOTTOM ->
                DoubleRectangle(
                    overallGeomBounds.left, overallTileBounds.top,
                    overallGeomBounds.width, overallTileBounds.height
                )
        }

        val horizontalAnchor = HorizontalAnchor.MIDDLE
        val verticalAnchor = when (orientation) {
            Orientation.LEFT, Orientation.RIGHT, Orientation.TOP -> VerticalAnchor.BOTTOM
            Orientation.BOTTOM -> VerticalAnchor.TOP
        }

        val rotation = when (orientation) {
            Orientation.LEFT -> -90.0
            Orientation.RIGHT -> -90.0
            else -> 0.0
        }

        val titleLocation = when (orientation) {
            Orientation.LEFT ->
                // Add 2 pix to the margin for better uppearence.
                DoubleVector(referenceRect.left - (PlotLayoutUtil.AXIS_TITLE_INNER_MARGIN + 2), referenceRect.center.y)
            Orientation.RIGHT ->
                DoubleVector(referenceRect.right + PlotLayoutUtil.AXIS_TITLE_INNER_MARGIN, referenceRect.center.y)
            Orientation.TOP ->
                DoubleVector(referenceRect.center.x, referenceRect.top - PlotLayoutUtil.AXIS_TITLE_INNER_MARGIN)
            Orientation.BOTTOM ->
                DoubleVector(referenceRect.center.x, referenceRect.bottom + PlotLayoutUtil.AXIS_TITLE_INNER_MARGIN)
        }

        val titleLabel = TextLabel(text)
        titleLabel.setHorizontalAnchor(horizontalAnchor)
        titleLabel.setVerticalAnchor(verticalAnchor)
        titleLabel.textColor().set(axisTheme.titleColor())
        titleLabel.moveTo(titleLocation)
        titleLabel.rotate(rotation)

        val titleElement = titleLabel.rootGroup
        titleElement.addClass(Style.AXIS_TITLE)

        // hack: we have style: ".axis .title text" and we don't want to break backward-compatibility with 'census' charts
        val parent = SvgGElement()
        parent.addClass(Style.AXIS)

        parent.children().add(titleElement)
        add(parent)
    }


    private fun drawDebugRect(r: DoubleRectangle, color: Color, message: String? = null) {
        val rect = SvgRectElement(r)
        rect.strokeColor().set(color)
        rect.strokeWidth().set(1.0)
        rect.fillOpacity().set(0.0)
        message?.run {
            onMouseMove(rect, "$message: $r")
        }
        add(rect)
    }

    /**
     * Only used when DEBUG_DRAWING is ON.
     *
     * Doesn't seem to work any longer
     */
    private fun onMouseMove(e: SvgElement, message: String) {
        e.addEventHandler(SvgEventSpec.MOUSE_MOVE, object :
            SvgEventHandler<Event> {
            override fun handle(node: SvgNode, e: Event) {
                println(message)
            }
        })
    }


    companion object {
        private val LOG = PortableLogging.logger(PlotSvgComponent::class)
        private const val DEBUG_DRAWING = PLOT_DEBUG_DRAWING
    }
}
