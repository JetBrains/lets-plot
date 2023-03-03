/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import jetbrains.datalore.base.event.dom.DomEventMapper
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.*
import jetbrains.datalore.base.js.css.enumerables.CssCursor
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.plot.livemap.LiveMapProviderUtil
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import jetbrains.datalore.vis.canvas.dom.DomEventPeer
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.dom.SvgRootDocumentMapper
import kotlinx.browser.document
import kotlinx.dom.createElement
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGSVGElement

internal class FigureToHtml(
    private val buildInfo: FigureBuildInfo,
    private val parentElement: HTMLElement
) {

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
                origin = null      // The topmost SVG
            )
        } else {
            processPlotFigure(
                svgRoot as PlotSvgRoot,
                origin = null      // The topmost SVG
            )
        }
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
        origin: DoubleVector?,
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
            wrapChildNode(domSVGSVG, origin)
        }

        parentElement.appendChild(rootNode)

        @Suppress("NAME_SHADOWING")
        val origin = origin ?: DoubleVector.ZERO

        // Sub-figures

        for (element in svgRoot.elements) {
            val elementOrigin = element.bounds.origin.add(origin)
            if (element is PlotSvgRoot) {
                processPlotFigure(element, elementOrigin)
            } else {
                element as CompositeFigureSvgRoot
                processCompositeFigure(element, elementOrigin)
            }
        }
    }

    private fun processPlotFigure(
        svgRoot: PlotSvgRoot,
        origin: DoubleVector?,
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

        val rootNode = if (origin == null) {
            rootSVG
        } else {
            wrapChildNode(rootSVG, origin)
        }

        parentElement.appendChild(rootNode)
    }


    companion object {
        fun setupRootHTMLElement(element: HTMLElement, size: DoubleVector) {
            var style = "position: relative; width: ${size.x}px; height: ${size.y}px;"

//    // 'background-color' makes livemap disappear - set only if no livemaps in the bunch.
//    if (!plotInfos.any { it.plotAssembler.containsLiveMap }) {
//        style = "$style background-color: ${Defaults.BACKDROP_COLOR};"
//    }
            element.setAttribute("style", style)
        }

        private fun wrapChildNode(node: Node, origin: DoubleVector): HTMLElement {
            return document.createElement("div") {
                setAttribute(
                    "style",
                    "position: absolute; left: ${origin.x}px; top: ${origin.y}px;"
                )

                appendChild(node)

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

            DomEventMapper(svg, plotContainer.mouseEventPeer::dispatch)

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

                DomEventPeer(
                    myEventTarget = svg,
                    myTargetBounds = DoubleRectangle.XYWH(bounds.origin.x,  bounds.origin.y, bounds.dimension.x, bounds.dimension.y),
                    destMouseEventPeer = canvasControl.mousePeer::dispatch
                )

                liveMapFigure.mapToCanvas(canvasControl)
                parentElement.appendChild(liveMapDiv)
            }

            return svg
        }
    }
}