/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.createElement
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.interact.event.UnsupportedToolEventDispatcher
import org.jetbrains.letsPlot.core.platf.dom.DomMouseEventMapper
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.core.plot.livemap.CursorServiceConfig
import org.jetbrains.letsPlot.core.plot.livemap.LiveMapProviderUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvasControl
import org.jetbrains.letsPlot.platf.w3c.dom.css.*
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssCursor
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssPosition
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.SvgRootDocumentMapper
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGSVGElement

internal class FigureToHtml(
    private val buildInfo: FigureBuildInfo,
//    private val containerElement: HTMLElement,
    private val parentElement: HTMLElement,
) {

//    private val parentElement: HTMLElement = if (buildInfo.isComposite) {
//        // The `containerElement` may also contain "computation messages".
//        // Container for a composite figure must be another `div`
//        // because it is going to have "relative" positioning.
//        document.createElement("div") {
//            containerElement.appendChild(this)
//        } as HTMLElement
//    } else {
//        containerElement
//    }

    fun eval(isRoot: Boolean): Result {

        val buildInfo = buildInfo.layoutedByOuterSize()
//        containerElement.style.apply {
//            width = "${buildInfo.layoutInfo.figureSize.x}px"
//            height = "${buildInfo.layoutInfo.figureSize.y}px"
//        }

        buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
            val cursorServiceConfig = CursorServiceConfig()
            LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
            cursorServiceConfig
        }

        val svgRoot = buildInfo.createSvgRoot()

        if (isRoot) {
            // Setup fixed dimentions for plot wrapper element.
            setupRootHTMLElement(
                parentElement,
                svgRoot.bounds.dimension
            )
        }

        val (toolEventDispatcher, eventsRegistration) = if (svgRoot is CompositeFigureSvgRoot) {
            processCompositeFigure(
                svgRoot,
                origin = null,      // The topmost SVG
                parentElement = parentElement,
            )
        } else {
            processPlotFigure(
                svgRoot = svgRoot as PlotSvgRoot,
                parentElement = parentElement,
                eventArea = buildInfo.bounds
            )
        }

        val domCleanupRegistration = object : Registration() {
            override fun doRemove() {
                while (parentElement.firstChild != null) {
                    parentElement.removeChild(parentElement.firstChild!!)
                }
            }
        }

        return Result(
            toolEventDispatcher,
            CompositeRegistration().add(
                eventsRegistration,
                domCleanupRegistration
            )
        )
    }

    data class Result(
        val toolEventDispatcher: ToolEventDispatcher,
        val figureRegistration: Registration
    )

    companion object {
        private fun processPlotFigure(
            svgRoot: PlotSvgRoot,
            parentElement: HTMLElement,
            eventArea: DoubleRectangle
        ): Pair<ToolEventDispatcher, Registration> {

            val plotContainer = PlotContainer(svgRoot)
            val (rootSVG, cleanupRegistration) = buildPlotFigureSVG(plotContainer, parentElement, eventArea)
            rootSVG.style.setCursor(CssCursor.CROSSHAIR)

            // Livemap cursor pointer
            if (svgRoot.isLiveMap) {
                val cursorServiceConfig = svgRoot.liveMapCursorServiceConfig as CursorServiceConfig
                cursorServiceConfig.defaultSetter { rootSVG.style.setCursor(CssCursor.CROSSHAIR) }
                cursorServiceConfig.pointerSetter { rootSVG.style.setCursor(CssCursor.POINTER) }
            }

            parentElement.appendChild(rootSVG)
            return plotContainer.toolEventDispatcher to cleanupRegistration
        }

        private fun processCompositeFigure(
            svgRoot: CompositeFigureSvgRoot,
            origin: DoubleVector?,
            parentElement: HTMLElement,
        ): Pair<ToolEventDispatcher, Registration> {
            svgRoot.ensureContentBuilt()

            val rootSvgSvg: SvgSvgElement = svgRoot.svg
            val domSVGSVG: SVGSVGElement = mapSvgToSVG(rootSvgSvg)
            val rootNode: Node = if (origin == null) {
//                setupRootHTMLElement(
//                    parentElement,
//                    svgRoot.bounds.dimension
//                )
                domSVGSVG
            } else {
                // Not a root - put in "container" with absolute positioning.
                createContainerElement(origin).apply {
                    appendChild(domSVGSVG)
                }
            }

            parentElement.appendChild(rootNode)

            @Suppress("NAME_SHADOWING")
            val origin = origin ?: DoubleVector.ZERO

            // Sub-figures

            for (figureSvgRoot in svgRoot.elements) {
                val elementOrigin = figureSvgRoot.bounds.origin.add(origin)
                if (figureSvgRoot is PlotSvgRoot) {
                    // Create "container" with absolute positioning.
                    val figureContainer = createContainerElement(elementOrigin).apply {
                        parentElement.appendChild(this)
                    }
                    processPlotFigure(
                        svgRoot = figureSvgRoot,
                        parentElement = figureContainer,
                        eventArea = figureSvgRoot.bounds.add(origin)
                    )
                } else {
                    figureSvgRoot as CompositeFigureSvgRoot
                    processCompositeFigure(figureSvgRoot, elementOrigin, parentElement)
                }
            }

            return UnsupportedToolEventDispatcher() to Registration.EMPTY
        }

        fun setupRootHTMLElement(element: HTMLElement, size: DoubleVector) {
//            val style = "position: relative;"  < -- ggbunch doesn't work without setting the container's width/height.
            val style = "position: relative; width: ${size.x}px; height: ${size.y}px;"
            element.setAttribute("style", style)
        }

        fun createContainerElement(origin: DoubleVector): HTMLElement {
            return document.createElement("div") {
                setAttribute(
                    "style",
                    "position: absolute; left: ${origin.x}px; top: ${origin.y}px;"
                )
            } as HTMLElement
        }

        private fun mapSvgToSVG(svg: SvgSvgElement): SVGSVGElement {
            val mapper = SvgRootDocumentMapper(svg)
            SvgNodeContainer(svg)
            mapper.attachRoot()
            return mapper.target
        }


        private fun buildPlotFigureSVG(
            plotContainer: PlotContainer,
            parentElement: Element,
            eventArea: DoubleRectangle,
        ): Pair<SVGSVGElement, Registration> {
            val svg: SVGSVGElement = mapSvgToSVG(plotContainer.svg)

            if (plotContainer.isLiveMap) {
                svg.style.run {
                    setPosition(CssPosition.RELATIVE)
                }
            }

            val plotMouseEventMapper = DomMouseEventMapper(parentElement, eventArea)

            val eventsRegistration = CompositeRegistration()
            eventsRegistration.add(Registration.from(plotMouseEventMapper))

            plotContainer.mouseEventPeer.addEventSource(plotMouseEventMapper)

            plotContainer.liveMapFigures.forEach { liveMapFigure ->
                val bounds = (liveMapFigure as CanvasFigure).bounds().get()
                val liveMapDiv = document.createElement("div") as HTMLElement

                liveMapDiv.style.run {
                    setLeft(bounds.origin.x.toDouble())
                    setTop(bounds.origin.y.toDouble())
                    setWidth(bounds.dimension.x)
                    setPosition(CssPosition.RELATIVE)
                }

                val canvasMouseEventMapper = DomMouseEventMapper(
                    parentElement,
                    DoubleRectangle(
                        eventArea.origin.add(bounds.origin.toDoubleVector()),
                        bounds.dimension.toDoubleVector()
                    )
                )
                eventsRegistration.add(Registration.from(canvasMouseEventMapper))

                val canvasControl = DomCanvasControl(
                    myRootElement = liveMapDiv,
                    size = Vector(bounds.dimension.x, bounds.dimension.y),
                    mouseEventSource = canvasMouseEventMapper
                )

                val liveMapReg = liveMapFigure.mapToCanvas(canvasControl)
                parentElement.appendChild(liveMapDiv)

                liveMapDiv.onDisconnect(liveMapReg::dispose)
            }

            return svg to eventsRegistration
        }

        private fun Node.onDisconnect(onDisconnected: () -> Unit): Int {
            fun checkConnection() {
                if (!isConnected) {
                    onDisconnected()
                } else {
                    window.requestAnimationFrame { checkConnection() }
                }
            }
            return window.requestAnimationFrame { checkConnection() }
        }
    }
}