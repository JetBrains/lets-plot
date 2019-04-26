package jetbrains.datalore.visualization.plot.gog.core.render.svg

import jetbrains.datalore.visualization.base.svg.SvgElement

/**
 * The counterpart of SVG 'stroke-dasharray' attribute but
 * length of alternating dashes and gaps
 * is defined as multiples of line width
 */
object StrokeDashArraySupport {
    fun apply(element: SvgElement, strokeWidth: Double, dashArray: List<Double>) {
        val sb = StringBuilder()
        for (relativeLength in dashArray) {
            val length = relativeLength * strokeWidth
            if (sb.length > 0) {
                sb.append(',')
            }
            sb.append(length.toString())
        }
        element.getAttribute("stroke-dasharray").set(sb.toString())
    }
}
