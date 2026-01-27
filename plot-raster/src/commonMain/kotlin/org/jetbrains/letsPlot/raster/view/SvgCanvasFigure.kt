/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.applyPath
import org.jetbrains.letsPlot.core.canvas.transform
import org.jetbrains.letsPlot.core.canvasFigure.AsyncRenderer
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContextListener
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.raster.mapping.svg.DebugOptions.drawBoundingBoxes
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.raster.scene.Container
import org.jetbrains.letsPlot.raster.scene.Node
import org.jetbrains.letsPlot.raster.scene.reversedDepthFirstTraversal
import kotlin.math.ceil

@Deprecated(
    "Migrate to SvgCanvasFigure and CanvasPane",
    replaceWith = ReplaceWith("SvgCanvasFigure", "org.jetbrains.letsPlot.raster.view")
)
typealias SvgCanvasFigure2 = SvgCanvasFigure

class SvgCanvasFigure(svg: SvgSvgElement = SvgSvgElement()) : CanvasFigure2 {
    override val size: Vector
        get() {
            val contentWidth = svgSvgElement.width().get()?.let { ceil(it).toInt() } ?: 0
            val contentHeight = svgSvgElement.height().get()?.let { ceil(it).toInt() } ?: 0
            return Vector(contentWidth, contentHeight)
        }

    override val mouseEventPeer: MouseEventPeer = MouseEventPeer()

    var svgSvgElement: SvgSvgElement = svg
        set(value) {
            field = value
            mapSvgSvgElement()
            requestRedraw()
        }

    private val renderingHints = mutableMapOf<Any, Any>()
    private var canvasSize: Vector? = null
    private var nodeContainer: SvgNodeContainer? = null
    private var svgCanvasPeer: SvgCanvasPeer? = null
    private var repaintManager: RepaintManager? = null
    private var asyncRenderers: MutableList<AsyncRenderer> = mutableListOf()

    internal lateinit var rootMapper: SvgSvgElementMapper
    private val repaintRequestListeners = mutableListOf<() -> Unit>()
    private var onHrefClick: ((String) -> Unit)? = null

    fun onHrefClick(handler: ((String) -> Unit)?) {
        onHrefClick = handler
    }

    override fun mapToCanvas(canvasPeer: CanvasPeer): Registration {
        svgCanvasPeer = SvgCanvasPeer(canvasPeer, onRepaintRequested = { requestRedraw() })
        repaintManager = RepaintManager(canvasPeer).also {
            val overscanFactor = renderingHints[RenderingHints.KEY_OVERSCAN_FACTOR] as? Double
            if (overscanFactor != null) {
                it.overscanFactor = overscanFactor
            }
        }
        mapSvgSvgElement()
        return object : Registration() {
            override fun doRemove() {
                rootMapper.detachRoot()

                svgCanvasPeer?.dispose()
                svgCanvasPeer = null

                repaintManager?.dispose()
                repaintManager = null
            }
        }
    }

    init {
        //setRenderingHint(RenderingHints.KEY_DEBUG_BBOXES, RenderingHints.VALUE_DEBUG_BBOXES_ON)
        setRenderingHint(RenderingHints.KEY_OFFSCREEN_BUFFERING, RenderingHints.VALUE_OFFSCREEN_BUFFERING_ON)
        setRenderingHint(RenderingHints.KEY_OVERSCAN_FACTOR, 2.5)

        mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_CLICKED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                val hrefClickHandler = onHrefClick ?: return
                val coord = event.location.toDoubleVector()
                val linkNode = reversedDepthFirstTraversal(
                    rootMapper.target
                )
                    .filter { it.href != null }
                    .filterNot(Node::isMouseTransparent)
                    .firstOrNull { coord in it.bBoxGlobal }

                val href = linkNode?.href ?: return
                hrefClickHandler(href)
            }
        })
    }

    private fun mapSvgSvgElement() {
        val canvasPeer = svgCanvasPeer ?: return
        nodeContainer = SvgNodeContainer(svgSvgElement)
        nodeContainer!!.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = requestRedraw()
            override fun onNodeAttached(node: SvgNode) = requestRedraw()
            override fun onNodeDetached(node: SvgNode) = requestRedraw()
        })
        rootMapper = SvgSvgElementMapper(svgSvgElement, canvasPeer)
        val ctx = MappingContext()
        ctx.addListener(object : MappingContextListener {
            override fun onMapperRegistered(mapper: Mapper<*, *>) {
                val node = mapper.target
                if (node is AsyncRenderer) {
                    asyncRenderers += node
                }
            }

            override fun onMapperUnregistered(mapper: Mapper<*, *>) {
                val node = mapper.target
                if (node is AsyncRenderer) {
                    asyncRenderers -= node
                }
            }
        })
        rootMapper.attachRoot(ctx)
    }

    override fun paint(context2d: Context2d) {
        renderElement(rootMapper.target, context2d)

        if (renderingHints[RenderingHints.KEY_DEBUG_BBOXES] == RenderingHints.VALUE_DEBUG_BBOXES_ON) {
            drawBoundingBoxes(rootMapper.target, context2d)
        }
    }

    override fun onRepaintRequested(listener: () -> Unit): Registration {
        repaintRequestListeners.add(listener)
        return Registration.onRemove { repaintRequestListeners.remove(listener) }
    }

    override fun resize(width: Number, height: Number) {
        canvasSize = Vector(width.toInt(), height.toInt())
        requestRedraw()
    }

    private fun requestRedraw() {
        repaintRequestListeners.forEach { it() }
    }

    private fun render(nodes: List<Node>, ctx: Context2d, ignoreCache: Boolean) {
        nodes.forEach { element ->
            renderElement(element, ctx, ignoreCache)
        }
    }

    private fun renderElement(node: Node, ctx: Context2d, ignoreCache: Boolean = false) {
        if (!node.isVisible) return

        var needRestore = false
        if (!node.transform.isIdentity) {
            needRestore = true
            ctx.save()
            ctx.transform(node.transform)
        }

        if (node.bufferedRendering && !ignoreCache
            && renderingHints[RenderingHints.KEY_OFFSCREEN_BUFFERING] == RenderingHints.VALUE_OFFSCREEN_BUFFERING_ON
        ) {
            val repaintManager = repaintManager ?: return

            if (!repaintManager.isCacheValid(node, size, ctx.contentScale)) {
                repaintManager.cacheElement(node, size, ctx.contentScale) {
                    renderElement(node, it, ignoreCache = true)
                }
                node.isDirty = false
            }

            repaintManager.paintElement(node, ctx)
            if (needRestore) ctx.restore()
            return
        }

        node.clipPath?.let { clipPath ->
            if (!needRestore) {
                ctx.save()
                needRestore = true
            }
            ctx.beginPath()
            ctx.applyPath(clipPath.getCommands())
            ctx.closePath()
            ctx.clip()
            ctx.save()
        }

        node.render(ctx)
        if (node is Container) {
            render(node.children, ctx, ignoreCache)
        }

        if (node.clipPath != null) {
            ctx.restore()
        }
        if (needRestore) {
            ctx.restore()
        }
    }

    fun setRenderingHint(key: Any, value: Any) {
        if (key == RenderingHints.KEY_OVERSCAN_FACTOR) {
            val factor = (value as? Number)?.toDouble() ?: return
            repaintManager?.overscanFactor = factor
        }
        renderingHints[key] = value
    }

    override fun isReady(): Boolean {
        return asyncRenderers.all(AsyncRenderer::isReady)
    }

    override fun onReady(listener: () -> Unit): Registration {
        if (isReady()) {
            listener()
            return Registration.EMPTY
        } else {
            val notReadyRenderers = asyncRenderers.filterNot(AsyncRenderer::isReady).toMutableSet()
            val regs = notReadyRenderers.map { renderer ->
                renderer.onReady {
                    notReadyRenderers.remove(renderer)
                    if (notReadyRenderers.isEmpty()) {
                        listener()
                    }
                }
            }

            return Registration.from(*regs.toTypedArray())
        }
    }

    override fun onFrame(millisTime: Long) {
        asyncRenderers.forEach { it.onFrame(millisTime) }
    }
}