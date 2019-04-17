package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.svg.SvgCircleElement
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.base.svg.SvgPathElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.SvgTextElement
import jetbrains.datalore.visualization.base.svg.SvgTextNode
import jetbrains.datalore.visualization.base.svg.slim.CanvasAware
import jetbrains.datalore.visualization.base.svg.slim.CanvasContext
import jetbrains.datalore.base.function.Runnable

import java.lang.Double.parseDouble
import jetbrains.datalore.visualization.base.canvas.CanvasControlUtil.drawLater
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STYLE_ATTRIBUTE

object SvgCanvasRenderer {
    fun draw(rootGroup: SvgGElement, canvasControl: CanvasControl) {
        val rootCanvas = canvasControl.createCanvas(canvasControl.size)
        canvasControl.addChildren(rootCanvas)

        val virtualCanvas = canvasControl.createCanvas(canvasControl.size)
        draw(rootGroup, Context2DCanvasContext(virtualCanvas.context2d))
        virtualCanvas.takeSnapshot().onSuccess(object : Consumer<Canvas.Snapshot> {
            override fun accept(value: Canvas.Snapshot) {
                drawLater(canvasControl, object : Runnable {
                    override fun run() {
                        rootCanvas.context2d.drawImage(value, 0, 0)
                    }
                })
            }
        })
    }

    private fun draw(g: SvgGElement, context: CanvasContext) {
        context.push(g.transform().get())
        for (node in g.children()) {
            when (node) {
                is CanvasAware -> node.draw(context)
                is SvgGElement -> draw(node, context)
                is SvgElement -> draw(node, context)
            }
        }
        context.restore()
    }

    private fun draw(el: SvgElement, context: CanvasContext) {
        when (el) {
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
                    stringOrNull(el.fill())!!,
                    oneIfNull(el.fillOpacity()),
                    stringOrNull(el.stroke()),
                    oneIfNull(el.strokeOpacity()),
                    zeroIfNull(el.strokeWidth())
            )
            is SvgPathElement -> context.drawPath(
                    stringOrNull(el.d())!!,
                    getDashArray(el),
                    stringOrNull(el.transform()),
                    stringOrNull(el.fill())!!,
                    oneIfNull(el.fillOpacity()),
                    stringOrNull(el.stroke()),
                    oneIfNull(el.strokeOpacity()),
                    zeroIfNull(el.strokeWidth())
            )
            is SvgTextElement -> context.drawText(
                    zeroIfNull(el.x()),
                    zeroIfNull(el.y()),
                    getText(el),
                    getStyle(el)!!,
                    stringOrNull(el.transform()),
                    stringOrNull(el.fill())!!,
                    oneIfNull(el.fillOpacity()),
                    stringOrNull(el.stroke()),
                    oneIfNull(el.strokeOpacity()),
                    zeroIfNull(el.strokeWidth())
            )
            else -> throw IllegalArgumentException("Unknown element with name: " + el.elementName)
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
        val arr = attr.toString().split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val dashes = DoubleArray(arr.size)
        for (i in arr.indices) {
            dashes[i] = parseDouble(arr[i])
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
}
