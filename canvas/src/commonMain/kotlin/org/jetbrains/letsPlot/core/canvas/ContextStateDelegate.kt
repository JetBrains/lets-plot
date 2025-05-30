/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.values.Color

class ContextStateDelegate(
    logEnabled: Boolean = true, failIfNotImplemented: Boolean = true
) : Context2d by Context2dDelegate(logEnabled = logEnabled, failIfNotImplemented = failIfNotImplemented) {
    private val state = ContextState()

    fun getCTM(): AffineTransform {
        return state.getCTM()
    }

    fun getCurrentPath(): List<Path2d.PathCommand> {
        return state.getCurrentPath()
    }

    fun getClipPath(): Path2d? {
        return state.getClipPath()
    }

    fun getLineDash(): List<Double> {
        return state.getLineDash()
    }

    fun getLineDashOffset(): Double {
        return state.getLineDashOffset()
    }

    override fun beginPath() {
        state.beginPath()
    }

    override fun closePath() {
        state.closePath()
    }

    override fun clip() {
        state.clip()
    }

    override fun moveTo(x: Double, y: Double) {
        state.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        state.lineTo(x, y)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        state.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun ellipse(
        x: Double,
        y: Double,
        radiusX: Double,
        radiusY: Double,
        rotation: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        state.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise)
    }

    override fun save() {
        state.save()
    }

    override fun restore() {
        state.restore()
    }

    override fun setFillStyle(color: Color?) {
        state.setFillStyle(color)
    }

    override fun setStrokeStyle(color: Color?) {
        state.setStrokeStyle(color)
    }

    override fun setGlobalAlpha(alpha: Double) {
        state.setGlobalAlpha(alpha)
    }

    override fun setFont(f: Font) {
        state.setFont(f)
    }

    override fun setLineWidth(lineWidth: Double) {
        state.setLineWidth(lineWidth)
    }

    override fun scale(x: Double, y: Double) {
        state.scale(x, y)
    }

    override fun scale(xy: Double) {
        state.scale(xy)
    }

    override fun rotate(angle: Double) {
        state.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        state.translate(x, y)
    }

    override fun transform(
        sx: Double,
        ry: Double,
        rx: Double,
        sy: Double,
        tx: Double,
        ty: Double
    ) {
        state.transform(sx, ry, rx, sy, tx, ty)
    }

    override fun bezierCurveTo(
        cp1x: Double,
        cp1y: Double,
        cp2x: Double,
        cp2y: Double,
        x: Double,
        y: Double
    ) {
        state.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        state.setLineJoin(lineJoin)
    }

    override fun setLineCap(lineCap: LineCap) {
        state.setLineCap(lineCap)
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        state.setStrokeMiterLimit(miterLimit)
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        state.setTextBaseline(baseline)
    }

    override fun setTextAlign(align: TextAlign) {
        state.setTextAlign(align)
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        state.setTransform(m00, m10, m01, m11, m02, m12)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        state.setLineDashPattern(lineDash.toList())
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        state.setLineDashOffset(lineDashOffset)
    }
}
