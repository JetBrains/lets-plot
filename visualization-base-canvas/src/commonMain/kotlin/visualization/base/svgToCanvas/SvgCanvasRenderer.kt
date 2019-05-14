package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.CanvasControlUtil.drawLater
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STYLE_ATTRIBUTE
import jetbrains.datalore.visualization.base.svg.slim.CanvasAware
import jetbrains.datalore.visualization.base.svg.slim.CanvasContext

object SvgCanvasRenderer {
    fun draw(rootGroup: SvgGElement, canvasControl: CanvasControl) {
        val rootCanvas = canvasControl.createCanvas(canvasControl.size)
        canvasControl.addChildren(rootCanvas)

        val virtualCanvas = canvasControl.createCanvas(canvasControl.size)
        draw(rootGroup, Context2DCanvasContext(virtualCanvas.context2d))

        virtualCanvas.takeSnapshot().onSuccess { value ->
            drawLater(canvasControl, object : Runnable {
                override fun run() {
                    rootCanvas.context2d.drawImage(value, 0, 0)
                }
            })
        }
    }

    private fun draw(root: SvgNode, context: CanvasContext) {
        when (root) {
            is CanvasAware -> root.draw(context)
            is SvgElement -> drawElement(root, context)
            else -> drawNode(root, context)
        }

        for (node in root.children()) {
            draw(node, context)
        }

        if (root is SvgGElement) {
            context.restore()
        }
    }

    private fun drawNode(node: SvgNode, context: CanvasContext) {
        println("Unknown svg-node with class: " + node::class)
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
                    firstNotNull(1.0, el.fillOpacity(), el.opacity()),
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
            else -> println("Unknown svg-element with name: " + el.elementName)
//            else -> throw IllegalArgumentException("Unknown element with name: " + el.elementName)
        }
    }

    private fun getText(element: SvgTextElement): String {
        val builder = StringBuilder()
        for (node in element.children()) {
            if (node is SvgTextNode) {
                builder.append(" ").append(node.textContent().get())
            }
        }
        return builder.toString().trim { it <= ' ' }
    }

    private fun getStyle(element: SvgTextElement): String? {
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

    private fun <T> firstNotNull(defaultValue: T, vararg values: Property<T?>): T {
        for (value in values) {
            val v = value.get()
            if (v != null) {
                return v
            }
        }
        return defaultValue
    }
}
