/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.plot.livemap.LiveMapProviderUtil
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.createElement
import org.jetbrains.letsPlot.platf.w3c.dom.css.*
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssCursor
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssPosition
import org.jetbrains.letsPlot.base.platf.dom.DomEventMapper
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.SvgRootDocumentMapper
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGSVGElement

internal class FigureToHtml(
    private val buildInfo: FigureBuildInfo,
    containerElement: HTMLElement
) {

    private val parentElement: HTMLElement = if (buildInfo.isComposite) {
        // The `containerElement` may also contain "computation messages".
        // Container for a composite figure must be another `div`
        // because it is going to have "relative" positioning.
        document.createElement("div") {
            containerElement.appendChild(this)
        } as HTMLElement
    } else {
        containerElement
    }

    fun eval() {

        val buildInfo = buildInfo.layoutedByOuterSize()

        buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
            val cursorServiceConfig = CursorServiceConfig()
            LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
            cursorServiceConfig
        }

        val svgRoot = buildInfo.createSvgRoot()
        if (svgRoot is CompositeFigureSvgRoot) {
            processCompositeFigure(
                svgRoot,
                origin = null,      // The topmost SVG
                parentElement = parentElement
            )
        } else {
            processPlotFigure(
                svgRoot as PlotSvgRoot,
                parentElement = parentElement
            )
        }
    }


    companion object {
        private fun processPlotFigure(
            svgRoot: PlotSvgRoot,
            parentElement: HTMLElement,
        ) {

            val plotContainer = PlotContainer(svgRoot)
            val rootSVG: SVGSVGElement = buildPlotFigureSVG(plotContainer, parentElement)
            rootSVG.style.setCursor(CssCursor.CROSSHAIR)

            // Livemap cursor pointer
            if (svgRoot.isLiveMap) {
                val cursorServiceConfig = svgRoot.liveMapCursorServiceConfig as CursorServiceConfig
                cursorServiceConfig.defaultSetter { rootSVG.style.setCursor(CssCursor.CROSSHAIR) }
                cursorServiceConfig.pointerSetter { rootSVG.style.setCursor(CssCursor.POINTER) }
            }

            parentElement.appendChild(rootSVG)
        }

        private fun processCompositeFigure(
            svgRoot: CompositeFigureSvgRoot,
            origin: DoubleVector?,
            parentElement: HTMLElement,
        ) {
            svgRoot.ensureContentBuilt()

            val rootSvgSvg: SvgSvgElement = svgRoot.svg
            val domSVGSVG: SVGSVGElement = mapSvgToSVG(rootSvgSvg)
            val rootNode: Node = if (origin == null) {
                setupRootHTMLElement(
                    parentElement,
                    svgRoot.bounds.dimension
                )
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
                        parentElement = figureContainer
                    )
                } else {
                    figureSvgRoot as CompositeFigureSvgRoot
                    processCompositeFigure(figureSvgRoot, elementOrigin, parentElement)
                }
            }
        }

        fun setupRootHTMLElement(element: HTMLElement, size: DoubleVector) {
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
            parentElement: Element
        ): SVGSVGElement {

            val svg: SVGSVGElement = mapSvgToSVG(plotContainer.svg)

            if (plotContainer.isLiveMap) {
                svg.style.run {
                    setPosition(CssPosition.RELATIVE)
                }
            }

            DomEventMapper(svg, destMouseEventPeer = plotContainer.mouseEventPeer::dispatch)

            plotContainer.liveMapFigures.forEach { liveMapFigure ->
                val bounds = (liveMapFigure as CanvasFigure).bounds().get()
                val liveMapDiv = document.createElement("div") as HTMLElement

                liveMapDiv.style.run {
                    setLeft(bounds.origin.x.toDouble())
                    setTop(bounds.origin.y.toDouble())
                    setWidth(bounds.dimension.x)
                    setPosition(CssPosition.RELATIVE)
                }

                val canvasControl = DomCanvasControl(
                    liveMapDiv,
                    Vector(bounds.dimension.x, bounds.dimension.y),
                )

                DomEventMapper(
                    myEventTarget = svg,
                    myTargetBounds = DoubleRectangle.XYWH(
                        bounds.origin.x,
                        bounds.origin.y,
                        bounds.dimension.x,
                        bounds.dimension.y
                    ),
                    destMouseEventPeer = canvasControl.mousePeer::dispatch
                )

                val liveMapReg = liveMapFigure.mapToCanvas(canvasControl)
                parentElement.appendChild(liveMapDiv)

                liveMapDiv.onDisconnect(liveMapReg::dispose)
            }

            return svg
        }

        private fun HTMLElement.onDisconnect(onDisconnected: () -> Unit): Int {
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