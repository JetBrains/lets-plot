package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.canvas.awt.AwtCanvas.AwtSnapshot
import java.awt.Graphics2D
import java.util.*
import java.awt.Color as AwtColor

internal class AwtContext2d(graphics2D: Graphics2D) : Context2d {
    private val myStack = Stack<Graphics2D>()

    init {
        myStack.push(graphics2D)
    }

    private fun current(): Graphics2D {
        return myStack.peek()
    }

    override fun clearRect(rect: DoubleRectangle) {
        save()
        current().background = AwtColor(128, 10, 250, 0)
        current().clearRect(rect.origin.x.toInt(), rect.origin.y.toInt(), rect.dimension.x.toInt(), rect.dimension.y.toInt())
        restore()
    }

    override fun drawImage(snapshot: Snapshot, x: Int, y: Int) {
        val awtSnapshot = snapshot as AwtSnapshot

        current().drawImage(
                awtSnapshot.image,
                x, y,
                awtSnapshot.size.x, awtSnapshot.size.y
        ) { img, infoflags, x1, y1, width, height -> false }
    }

    override fun beginPath() {
        // ToDo:
    }

    override fun closePath() {
        // ToDo:
    }

    override fun stroke() {
        // ToDo:
    }

    override fun fill() {
        throw IllegalStateException("Not implemented")
    }

    override fun fillEvenOdd() {
        throw IllegalStateException("Not implemented")
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        current().fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun moveTo(x: Double, y: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun lineTo(x: Double, y: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {
        // ToDo:
    }

    override fun save() {
        myStack.add(current().create() as Graphics2D)
    }

    override fun restore() {
        myStack.pop()
    }

    override fun setFillColor(color: Color?) {
        current().color = if (color != null) AwtColor.decode(color.toHexColor()) else null
    }

    override fun setStrokeColor(color: Color?) {
        current().color = if (color != null) AwtColor.decode(color.toHexColor()) else null
    }

    override fun setGlobalAlpha(alpha: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun setFont(f: String) {
        // ToDo: change interface
    }

    override fun setLineWidth(lineWidth: Double) {
        // ToDo:
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        current().drawString(text, x.toInt(), y.toInt())
    }

    override fun fillText(text: String, x: Double, y: Double) {
        // ToDo:
    }

    override fun scale(x: Double, y: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun rotate(angle: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun translate(x: Double, y: Double) {
        current().translate(x, y)
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) {
        throw IllegalStateException("Not implemented")
    }

    override fun setLineJoin(lineJoin: Context2d.LineJoin) {
        throw IllegalStateException("Not implemented")
    }

    override fun setLineCap(lineCap: Context2d.LineCap) {
        throw IllegalStateException("Not implemented")
    }

    override fun setTextBaseline(baseline: Context2d.TextBaseline) {
        // ToDo:
    }

    override fun setTextAlign(align: Context2d.TextAlign) {
        // ToDo:
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        // ToDo:
        // current().setTransform(new AffineTransform(m11, m12, m21, m22, dx, dy));
    }

    override fun setLineDash(lineDash: DoubleArray) {
        throw IllegalStateException("Not implemented")
    }

    override fun measureText(s: String): Double {
        return current().font.getStringBounds(s, current().fontRenderContext).width
    }
}
