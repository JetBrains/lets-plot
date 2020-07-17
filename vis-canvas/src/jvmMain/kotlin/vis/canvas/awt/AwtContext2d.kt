/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import java.awt.Graphics

internal class AwtContext2d(private val myContext2d: Graphics) : Context2d {
    override fun clearRect(rect: DoubleRectangle) {
        TODO("Not yet implemented")
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        TODO("Not yet implemented")
    }

    override fun drawImage(
        snapshot: Canvas.Snapshot,
        sx: Double,
        sy: Double,
        sw: Double,
        sh: Double,
        dx: Double,
        dy: Double,
        dw: Double,
        dh: Double
    ) {
        TODO("Not yet implemented")
    }

    override fun beginPath() {
        TODO("Not yet implemented")
    }

    override fun closePath() {
        TODO("Not yet implemented")
    }

    override fun stroke() {
        TODO("Not yet implemented")
    }

    override fun fill() {
        TODO("Not yet implemented")
    }

    override fun fillEvenOdd() {
        TODO("Not yet implemented")
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        TODO("Not yet implemented")
    }

    override fun moveTo(x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun lineTo(x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    override fun restore() {
        TODO("Not yet implemented")
    }

    override fun setFillStyle(color: String?) {
        TODO("Not yet implemented")
    }

    override fun setStrokeStyle(color: String?) {
        TODO("Not yet implemented")
    }

    override fun setGlobalAlpha(alpha: Double) {
        TODO("Not yet implemented")
    }

    override fun setFont(f: String) {
        TODO("Not yet implemented")
    }

    override fun setLineWidth(lineWidth: Double) {
        TODO("Not yet implemented")
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        TODO("Not yet implemented")
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun fillText(text: String, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun scale(x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun rotate(angle: Double) {
        TODO("Not yet implemented")
    }

    override fun translate(x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        TODO("Not yet implemented")
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun setLineJoin(lineJoin: Context2d.LineJoin) {
        TODO("Not yet implemented")
    }

    override fun setLineCap(lineCap: Context2d.LineCap) {
        TODO("Not yet implemented")
    }

    override fun setTextBaseline(baseline: Context2d.TextBaseline) {
        TODO("Not yet implemented")
    }

    override fun setTextAlign(align: Context2d.TextAlign) {
        TODO("Not yet implemented")
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        TODO("Not yet implemented")
    }

    override fun setLineDash(lineDash: DoubleArray) {
        TODO("Not yet implemented")
    }

    override fun measureText(str: String): Double {
        TODO("Not yet implemented")
    }

    override fun measureText(str: String, font: String): DoubleVector {
        TODO("Not yet implemented")
    }

}