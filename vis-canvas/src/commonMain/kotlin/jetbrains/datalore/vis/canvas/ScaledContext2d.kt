/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Canvas.Snapshot

internal class ScaledContext2d(private val myContext2d: Context2d, private val myScale: Double) :
    Context2d {

    private fun scaled(value: Double): Double {
        return myScale * value
    }

    private fun descaled(value: Double): Double {
        return value / myScale
    }

    private fun descaled(value: DoubleVector): DoubleVector {
        return value.mul(1.0 / myScale)
    }

    private fun scaled(values: DoubleArray): DoubleArray {
        if (myScale == 1.0) {
            return values
        }
        val res = DoubleArray(values.size)
        for (i in values.indices) {
            res[i] = scaled(values[i])
        }
        return res
    }

    private fun scaled(font: String): String {
        return CssStyleUtil.scaleFont(font, myScale)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) {
        myContext2d.drawImage(snapshot, scaled(x), scaled(y))
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        myContext2d.drawImage(snapshot, scaled(x), scaled(y), scaled(dw), scaled(dh))
    }

    override fun drawImage(
        snapshot: Snapshot,
        sx: Double,
        sy: Double,
        sw: Double,
        sh: Double,
        dx: Double,
        dy: Double,
        dw: Double,
        dh: Double
    ) {
        myContext2d.drawImage(
            snapshot,
            sx,
            sy,
            sw,
            sh,
            scaled(dx),
            scaled(dy),
            scaled(dw),
            scaled(dh)
        )
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
        myContext2d.fill()
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        myContext2d.fillRect(scaled(x), scaled(y), scaled(w), scaled(h))
    }

    override fun moveTo(x: Double, y: Double) {
        myContext2d.moveTo(scaled(x), scaled(y))
    }

    override fun lineTo(x: Double, y: Double) {
        myContext2d.lineTo(scaled(x), scaled(y))
    }

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {
        myContext2d.arc(scaled(x), scaled(y), scaled(radius), startAngle, endAngle)
    }

    override fun save() {
        myContext2d.save()
    }

    override fun restore() {
        myContext2d.restore()
    }

    override fun setFillStyle(color: String?) {
        myContext2d.setFillStyle(color)
    }

    override fun setStrokeStyle(color: String?) {
        myContext2d.setStrokeStyle(color)
    }

    override fun setGlobalAlpha(alpha: Double) {
        myContext2d.setGlobalAlpha(alpha)
    }

    override fun setFont(f: String) {
        myContext2d.setFont(scaled(f))
    }

    override fun setLineWidth(lineWidth: Double) {
        myContext2d.setLineWidth(scaled(lineWidth))
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        myContext2d.strokeRect(scaled(x), scaled(y), scaled(w), scaled(h))
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        myContext2d.strokeText(text, scaled(x), scaled(y))
    }

    override fun fillText(text: String, x: Double, y: Double) {
        myContext2d.fillText(text, scaled(x), scaled(y))
    }

    override fun scale(x: Double, y: Double) {
        myContext2d.scale(x, y)
    }

    override fun rotate(angle: Double) {
        myContext2d.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        myContext2d.translate(scaled(x), scaled(y))
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.transform(m11, m12, m21, m22, scaled(dx), scaled(dy))
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        myContext2d.bezierCurveTo(scaled(cp1x), scaled(cp1y), scaled(cp2x), scaled(cp2y), scaled(x), scaled(y))
    }

    override fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) {
        myContext2d.quadraticCurveTo(scaled(cpx), scaled(cpy), scaled(x), scaled(y))
    }

    override fun setLineJoin(lineJoin: Context2d.LineJoin) {
        myContext2d.setLineJoin(lineJoin)
    }

    override fun setLineCap(lineCap: Context2d.LineCap) {
        myContext2d.setLineCap(lineCap)
    }

    override fun setTextBaseline(baseline: Context2d.TextBaseline) {
        myContext2d.setTextBaseline(baseline)
    }

    override fun setTextAlign(align: Context2d.TextAlign) {
        myContext2d.setTextAlign(align)
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.setTransform(m11, m12, m21, m22, scaled(dx), scaled(dy))
    }

    override fun fillEvenOdd() {
        myContext2d.fillEvenOdd()
    }

    override fun setLineDash(lineDash: DoubleArray) {
        myContext2d.setLineDash(scaled(lineDash))
    }

    override fun measureText(str: String): Double {
        return descaled(myContext2d.measureText(str))
    }

    override fun measureText(str: String, font: String): DoubleVector {
        return descaled(myContext2d.measureText(str, scaled(font)))
    }

    override fun clearRect(rect: DoubleRectangle) {
        myContext2d.clearRect(DoubleRectangle(rect.origin.mul(2.0), rect.dimension.mul(2.0)))
    }
}
