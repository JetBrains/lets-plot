package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.FillRule
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.canvas.CssFontParser

internal class JavafxContext2d(private val myContext2d: GraphicsContext) : Context2d {
    private fun convertLineJoin(lineJoin: Context2d.LineJoin): StrokeLineJoin {
        when (lineJoin) {
            Context2d.LineJoin.BEVEL -> return StrokeLineJoin.BEVEL
            Context2d.LineJoin.MITER -> return StrokeLineJoin.MITER
            Context2d.LineJoin.ROUND -> return StrokeLineJoin.ROUND
        }

        throw IllegalStateException("Unknown LineJoin value: $lineJoin")
    }

    private fun convertLineCap(lineCap: Context2d.LineCap): StrokeLineCap {
        when (lineCap) {
            Context2d.LineCap.BUTT -> return StrokeLineCap.BUTT
            Context2d.LineCap.ROUND -> return StrokeLineCap.ROUND
            Context2d.LineCap.SQUARE -> return StrokeLineCap.SQUARE
        }

        throw IllegalStateException("Unknown LineCap value: $lineCap")
    }

    private fun convertTextBaseline(baseline: Context2d.TextBaseline): VPos {
        when (baseline) {
            Context2d.TextBaseline.ALPHABETIC -> return VPos.BOTTOM
            Context2d.TextBaseline.BOTTOM -> return VPos.BOTTOM
            Context2d.TextBaseline.HANGING -> return VPos.TOP
            Context2d.TextBaseline.IDEOGRAPHIC -> return VPos.BOTTOM
            Context2d.TextBaseline.MIDDLE -> return VPos.CENTER
            Context2d.TextBaseline.TOP -> return VPos.TOP
        }

        throw IllegalStateException("Unknown TextBaseline value: $baseline")
    }

    private fun convertTextAlign(align: Context2d.TextAlign): TextAlignment {
        when (align) {
            Context2d.TextAlign.CENTER -> return TextAlignment.CENTER
            Context2d.TextAlign.END -> return TextAlignment.RIGHT
            Context2d.TextAlign.LEFT -> return TextAlignment.LEFT
            Context2d.TextAlign.RIGHT -> return TextAlignment.RIGHT
            Context2d.TextAlign.START -> return TextAlignment.LEFT
        }

        throw IllegalStateException("Unknown TextAlign value: $align")
    }

    private fun convertCssColor(colorString: String?): Color? {
        return if (colorString == null || "none" == colorString) null else Color.valueOf(colorString)
    }

    private fun convertCssFont(fontString: String): Font {
        val parser = CssFontParser.create(fontString)
                ?: throw IllegalStateException("Could not parse css font string: $fontString")

        val family = parser.fontFamily
        val size = parser.fontSize
        return if (size == null) Font.font(family) else Font.font(family, size)
    }

    override fun drawImage(snapshot: Snapshot, x: Int, y: Int) {
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, x.toDouble(), y.toDouble())
    }

    override fun beginPath() {
        myContext2d.beginPath()
    }

    override fun closePath() {
        myContext2d.closePath()
    }

    override fun stroke() {
        myContext2d.stroke()
    }

    override fun fill() {
        fill(FillRule.NON_ZERO)
    }

    override fun fillEvenOdd() {
        fill(FillRule.EVEN_ODD)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        myContext2d.fillRect(x, y, w, h)
    }

    override fun moveTo(x: Double, y: Double) {
        myContext2d.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        myContext2d.lineTo(x, y)
    }

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {
        myContext2d.arc(x, y, radius, radius, -toDegrees(startAngle), toDegrees(startAngle) - toDegrees(endAngle))
    }

    override fun save() {
        myContext2d.save()
    }

    override fun restore() {
        myContext2d.restore()
    }

    override fun setFillStyle(fillStyleColor: String) {
        myContext2d.fill = convertCssColor(fillStyleColor)
    }

    override fun setStrokeStyle(strokeStyleColor: String?) {
        myContext2d.stroke = convertCssColor(strokeStyleColor)
    }

    override fun setGlobalAlpha(alpha: Double) {
        myContext2d.globalAlpha = alpha
    }

    override fun setFont(f: String) {
        myContext2d.font = convertCssFont(f)
    }

    override fun setLineWidth(lineWidth: Double) {
        myContext2d.lineWidth = lineWidth
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        myContext2d.strokeRect(x, y, w, h)
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        myContext2d.strokeText(text, x, y)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        myContext2d.fillText(text, x, y)
    }

    override fun scale(x: Double, y: Double) {
        myContext2d.scale(x, y)
    }

    override fun rotate(angle: Double) {
        myContext2d.rotate(toDegrees(angle))
    }

    override fun translate(x: Double, y: Double) {
        myContext2d.translate(x, y)
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.transform(m11, m12, m21, m22, dx, dy)
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        myContext2d.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) {
        myContext2d.quadraticCurveTo(cpx, cpy, x, y)
    }

    override fun setLineJoin(lineJoin: Context2d.LineJoin) {
        myContext2d.lineJoin = convertLineJoin(lineJoin)
    }

    override fun setLineCap(lineCap: Context2d.LineCap) {
        myContext2d.lineCap = convertLineCap(lineCap)
    }

    override fun setTextBaseline(baseline: Context2d.TextBaseline) {
        myContext2d.textBaseline = convertTextBaseline(baseline)
    }

    override fun setTextAlign(align: Context2d.TextAlign) {
        myContext2d.textAlign = convertTextAlign(align)
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.setTransform(m11, m12, m21, m22, dx, dy)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        myContext2d.setLineDashes(*lineDash)
    }

    override fun measureText(s: String): Double {
        val text = Text(s)
        val font = myContext2d.font
        text.font = font
        return text.layoutBounds.width
    }

    override fun clearRect(rect: DoubleRectangle) {
        myContext2d.clearRect(rect.left, rect.top, rect.width, rect.height)
    }

    private fun fill(fillRule: FillRule) {
        myContext2d.fillRule = fillRule
        myContext2d.fill()
    }
}
