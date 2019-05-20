package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.CanvasControlUtil.drawLater
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STYLE_ATTRIBUTE
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent
import jetbrains.datalore.visualization.base.svg.slim.CanvasAware
import jetbrains.datalore.visualization.base.svg.slim.CanvasContext

class SvgCanvasRenderer(private val svgRoot: SvgElement, private val canvasControl: CanvasControl) : CanvasControl.AnimationEventHandler, Disposable {
    private val mainCanvas = canvasControl.createCanvas(canvasControl.size)
    private val virtualCanvas = canvasControl.createCanvas(canvasControl.size)
    private val animationTimer = canvasControl.createAnimationTimer(this)
    private var needRedraw: Boolean = true

    init {
        canvasControl.addChild(mainCanvas)
        initNodeContainer()
        animationTimer.start()
    }

    private fun initNodeContainer() {
        val svgPlatformPeer = SvgPlatformPeerImpl(virtualCanvas.context2d)
        val svgSvgElement = svgSvgElement(svgRoot)

        val svgNodeContainer = SvgNodeContainer(svgSvgElement)
        svgNodeContainer.setPeer(svgPlatformPeer)
        svgNodeContainer.addListener(object : SvgNodeContainerAdapter() {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {
                svgPlatformPeer.invalidate(element)
                needRedraw = true
            }

            override fun onNodeAttached(node: SvgNode) {
                needRedraw = true
            }

            override fun onNodeDetached(node: SvgNode) {
                needRedraw = true
            }
        })
    }

    override fun onEvent(millisTime: Long): Boolean {
        return if (needRedraw) {
            redraw()
            needRedraw = false
            true
        } else {
            false
        }
    }

    override fun dispose() {
        animationTimer.stop()
    }

    private fun redraw() {
        clearCanvas(virtualCanvas)
        draw(svgRoot, Context2DCanvasContext(virtualCanvas.context2d))
        virtualCanvas.takeSnapshot().onSuccess { value ->
            drawLater(canvasControl) {
                clearCanvas(mainCanvas)
                mainCanvas.context2d.drawImage(value, 0.0, 0.0)
            }
        }
    }

    companion object {
        private fun clearCanvas(canvas: Canvas) {
            canvas.context2d.clearRect(DoubleRectangle(0.0, 0.0, canvas.size.x.toDouble(), canvas.size.y.toDouble()))
        }

        private fun svgSvgElement(svgElement: SvgElement): SvgSvgElement {
            return if (svgElement is SvgSvgElement) {
                svgElement
            } else {
                val svgSvgElement = SvgSvgElement()
                svgSvgElement.children().add(svgElement)
                svgSvgElement
            }
        }

        private fun draw(root: SvgNode, context: CanvasContext) {
            if (root is SvgGraphicsElement && root.visibility().get() == SvgGraphicsElement.Visibility.HIDDEN) {
                return
            }

            when (root) {
                is CanvasAware -> root.draw(context)
                is SvgElement -> drawElement(root, context)
                else -> Unit
            }

            for (node in root.children()) {
                draw(node, context)
            }

            if (root is SvgGElement) {
                context.restore()
            }
        }

        private fun drawElement(el: SvgElement, context: CanvasContext) {
            when (el) {
                is SvgSvgElement -> Unit //TODO:
                is SvgStyleElement -> Unit //TODO:
                is SvgGElement -> context.push(
                        el.transform().get()
                )
                is SvgCircleElement -> context.drawCircle(
                        zeroIfNull(el.cx()),
                        zeroIfNull(el.cy()),
                        zeroIfNull(el.r()),
                        getDashArray(el),
                        stringOrNull(el.transform()),
                        stringOrNull(el.fill())!!,
                        oneIfNull(el.fillOpacity()),
                        stringOrNull(el.stroke()),
                        oneIfNull(el.strokeOpacity()),
                        zeroIfNull(el.strokeWidth())
                )
                is SvgLineElement -> context.drawLine(
                        zeroIfNull(el.x1()),
                        zeroIfNull(el.y1()),
                        zeroIfNull(el.x2()),
                        zeroIfNull(el.y2()),
                        getDashArray(el),
                        stringOrNull(el.transform()),
                        stringOrNull(el.stroke()),
                        oneIfNull(el.strokeOpacity()),
                        zeroIfNull(el.strokeWidth())
                )
                is SvgRectElement -> context.drawRect(
                        zeroIfNull(el.x()),
                        zeroIfNull(el.y()),
                        zeroIfNull(el.width()),
                        zeroIfNull(el.height()),
                        getDashArray(el),
                        stringOrNull(el.transform()),
                        stringOrNull(el.fill()),
                        oneIfNull(el.fillOpacity()),
                        stringOrNull(el.stroke()),
                        oneIfNull(el.strokeOpacity()),
                        zeroIfNull(el.strokeWidth())
                )
                is SvgPathElement -> context.drawPath(
                        stringOrNull(el.d()),
                        getDashArray(el),
                        stringOrNull(el.transform()),
                        stringOrNull(el.fill()),
                        oneIfNull(el.fillOpacity()),
                        stringOrNull(el.stroke()),
                        oneIfNull(el.strokeOpacity()),
                        zeroIfNull(el.strokeWidth())
                )
                is SvgTextElement -> context.drawText(
                        zeroIfNull(el.x()),
                        zeroIfNull(el.y()),
                        getText(el),
                        getStyle(el),
                        stringOrNull(el.transform()),
                        stringOrNull(el.fill()),
                        oneIfNull(el.fillOpacity()),
                        stringOrNull(el.stroke()),
                        oneIfNull(el.strokeOpacity()),
                        zeroIfNull(el.strokeWidth()),
                        stringOrNull(el.textAnchor()),
                        stringOrNull(el.textDy())
                )
                is SvgTSpanElement -> context.drawText(
                        zeroIfNull(el.x()),
                        zeroIfNull(el.y()),
                        getText(el),
                        getStyle(el),
                        null,
                        stringOrNull(el.fill()),
                        oneIfNull(el.fillOpacity()),
                        stringOrNull(el.stroke()),
                        oneIfNull(el.strokeOpacity()),
                        zeroIfNull(el.strokeWidth()),
                        stringOrNull(el.textAnchor()),
                        stringOrNull(el.textDy())
                )
                else -> println("Unknown svg-element with name: " + el.elementName)
            }
        }

        private fun getText(element: SvgElement, deep: Boolean = false): String {
            val builder = StringBuilder()
            for (node in element.children()) {
                if (deep && node is SvgElement) {
                    builder.append(" ").append(getText(node))
                } else if (node is SvgTextNode) {
                    builder.append(" ").append(node.textContent().get())
                }
            }
            return builder.toString().trim { it <= ' ' }
        }

        private fun getStyle(element: SvgElement): String? {
            val attr = element.getAttribute(SVG_STYLE_ATTRIBUTE).get()
            return attr?.toString()
        }

        private fun getDashArray(element: SvgElement): DoubleArray? {
            val attr = element.getAttribute(SVG_STROKE_DASHARRAY_ATTRIBUTE).get() ?: return null
            val arr = attr.toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val dashes = DoubleArray(arr.size)
            for (i in arr.indices) {
                dashes[i] = arr[i].toDouble()
            }
            return dashes
        }

        private fun zeroIfNull(p: Property<Double?>): Double {
            return if (p.get() == null) 0.0 else p.get()!!
        }

        private fun oneIfNull(p: Property<Double?>): Double {
            return if (p.get() == null) 1.0 else p.get()!!
        }

        private fun stringOrNull(p: Property<*>): String? {
            return if (p.get() == null) null else p.get().toString()
        }

        private class SvgPlatformPeerImpl(private val context2d: Context2d) : SvgPlatformPeer {
            private val myMappingMap = HashMap<SvgLocatable, Pair<String, DoubleRectangle>>()

            override fun getComputedTextLength(node: SvgTextContent): Double {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getBBox(element: SvgLocatable): DoubleRectangle {
                if (element is SvgTextElement) {
                    val pair = myMappingMap[element]
                    val text = getText(element, true)
                    if (pair == null || pair.first != text) {
                        val rect = calculateTextBBox(element, text)
                        myMappingMap[element] = Pair(text, rect)
                    }
                    return myMappingMap[element]!!.second
                }
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            private fun calculateTextBBox(element: SvgTextElement, text: String? = null): DoubleRectangle {
                val str = text ?: getText(element, true)
                val size = context2d.measureText(str, Context2DCanvasContext.extractFont(getStyle(element)))
                return DoubleRectangle(element.x().get()!!, element.y().get()!!, size.x, size.y)
            }

            fun invalidate(element: SvgElement) {
                if (element is SvgLocatable) {
                    myMappingMap.remove(element)
                }
            }
        }
    }
}
