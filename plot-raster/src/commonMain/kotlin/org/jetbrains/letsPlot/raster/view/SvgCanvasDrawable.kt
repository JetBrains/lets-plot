/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContextListener
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.raster.mapping.svg.DebugOptions.drawBoundingBoxes
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.raster.scene.CanvasNode
import org.jetbrains.letsPlot.raster.scene.Container
import org.jetbrains.letsPlot.raster.scene.Node
import org.jetbrains.letsPlot.raster.scene.reversedDepthFirstTraversal
import kotlin.math.ceil

@Deprecated(
    "Migrate to SvgCanvasFigure and CanvasPane",
    replaceWith = ReplaceWith("SvgCanvasFigure", "org.jetbrains.letsPlot.raster.view")
)
typealias SvgCanvasFigure2 = SvgCanvasDrawable

class SvgCanvasDrawable(svg: SvgSvgElement = SvgSvgElement()) : CanvasDrawable {
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
    private var svgCanvasPeer: SvgCanvasPeer? = null
    private var repaintManager: RepaintManager? = null
    private var svgMappingContext: SvgMappingContext? = null
    private var redrawPending: Boolean = false
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
                svgMappingContext?.dispose()
                svgMappingContext = null

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
                val rootNode = svgMappingContext?.rootMapper?.target ?: return
                val linkNode = reversedDepthFirstTraversal(rootNode)
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
        svgMappingContext?.dispose()
        svgMappingContext = SvgMappingContext(svgSvgElement, canvasPeer)
    }

    override fun paint(context2d: Context2d) {
        redrawPending = false
        val rootNode = svgMappingContext?.rootMapper?.target ?: return
        renderElement(rootNode, context2d)

        if (renderingHints[RenderingHints.KEY_DEBUG_BBOXES] == RenderingHints.VALUE_DEBUG_BBOXES_ON) {
            drawBoundingBoxes(rootNode, context2d)
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
        if (redrawPending) {
            return
        }

        redrawPending = true
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
        return svgMappingContext?.isReady() ?: true
    }

    override fun onReady(listener: () -> Unit): Registration {
        return svgMappingContext?.onReady(listener) ?: Registration.EMPTY
    }

    override fun onFrame(millisTime: Long) {
        svgMappingContext?.onFrame(millisTime)
    }

    private inner class SvgMappingContext(
        svgRoot: SvgSvgElement,
        canvasPeer: SvgCanvasPeer,
    ) : Disposable {
        val canvasNodes: MutableMap<CanvasNode, Registration> = mutableMapOf()
        val rootMapper: SvgSvgElementMapper = SvgSvgElementMapper(svgRoot, canvasPeer)

        private val container = SvgNodeContainer(svgRoot)
        private val reg: Registration

        init {
            val containerReg = container.addListener(object : SvgNodeContainerListener {
                override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = requestRedraw()
                override fun onNodeAttached(node: SvgNode) = requestRedraw()
                override fun onNodeDetached(node: SvgNode) = requestRedraw()
            })

            val ctx = MappingContext()
            ctx.addListener(object : MappingContextListener {
                override fun onMapperRegistered(mapper: Mapper<*, *>) {
                    val node = mapper.target
                    if (node is CanvasNode) {
                        canvasNodes[node] = node.mouseEventPeer.addEventSource(mouseEventPeer)
                    }
                }

                override fun onMapperUnregistered(mapper: Mapper<*, *>) {
                    val node = mapper.target as Node
                    if (node is CanvasNode) {
                        canvasNodes.remove(node)?.remove()
                    }
                    repaintManager?.remove(node)
                }
            })
            rootMapper.attachRoot(ctx)

            reg = CompositeRegistration(
                containerReg,
                Registration.onRemove {
                    canvasNodes.values.forEach(Registration::dispose)
                    canvasNodes.clear()
                },
                Registration.onRemove {
                    container.root().set(SvgSvgElement())
                },
                Registration.onRemove {
                    rootMapper.detachRoot()
                }
            )
        }

        override fun dispose() {
            reg.dispose()
        }

        fun isReady(): Boolean {
            return canvasNodes.keys.all(AsyncRenderer::isReady)
        }

        fun onReady(listener: () -> Unit): Registration {
            if (isReady()) {
                listener()
                return Registration.EMPTY
            }

            val notReadyRenderers = canvasNodes.keys.filterNot(AsyncRenderer::isReady)
            var remaining = notReadyRenderers.size
            val regs = notReadyRenderers.map { renderer ->
                renderer.onReady {
                    remaining--
                    if (remaining == 0) {
                        listener()
                    }
                }
            }

            return Registration.from(*regs.toTypedArray())
        }

        fun onFrame(millisTime: Long) {
            canvasNodes.forEach { (canvasNode, _) -> canvasNode.onFrame(millisTime) }
        }
    }
}
