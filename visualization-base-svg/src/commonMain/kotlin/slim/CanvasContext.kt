package jetbrains.datalore.visualization.base.svg.slim

interface CanvasContext {
    fun push(transform: Any?)

    fun restore()

    fun drawCircle(cx: Double, cy: Double, r: Double, lineDash: DoubleArray?, transform: String?,
                   fillColor: String?, fillOpacity: Double, strokeColor: String?, strokeOpacity: Double, strokeWidth: Double)

    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double, lineDash: DoubleArray?, transform: String?,
                 strokeColor: String?, strokeOpacity: Double, strokeWidth: Double)

    fun drawRect(x: Double, y: Double, width: Double, height: Double, lineDash: DoubleArray?, transform: String?,
                 fillColor: String?, fillOpacity: Double, strokeColor: String?, strokeOpacity: Double, strokeWidth: Double)

    fun drawPath(d: String?, lineDash: DoubleArray?, transform: String?,
                 fillColor: String?, fillOpacity: Double, strokeColor: String?, strokeOpacity: Double, strokeWidth: Double)

    fun drawText(x: Double, y: Double, text: String, style: String, transform: String?,
                 fillColor: String, fillOpacity: Double, strokeColor: String?, strokeOpacity: Double, strokeWidth: Double)
}
