/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Path2d.PathCommand

private const val logEnabled = false
private fun log(str: () -> String) {
    if (logEnabled)
        println(str())
}

class ContextState {
    private val states = ArrayList<StateEntry>()
    private var currentState = StateEntry.create()
    private var currentPath: Path2d = Path2d()

    fun getCurrentState(): StateEntry {
        return currentState.copy()
    }

    fun getCTM(): AffineTransform {
        return currentState.transform
    }

    fun getCurrentPath(): List<PathCommand> {
        return currentPath.getCommands()
    }

    fun getClipPath(): Path2d? {
        return currentState.clipPath
    }

    class StateEntry(
        var strokeColor: Color,
        var strokeWidth: Double,
        var lineDashPattern: List<Double>?,
        var lineDashOffset: Double,
        var miterLimit: Double,
        var lineCap: LineCap,
        var lineJoin: LineJoin,
        var fillColor: Color,
        var font: Font,
        var transform: AffineTransform,
        var clipPath: Path2d? = null
    ) {
        fun copy(): StateEntry {
            return StateEntry(
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                lineDashOffset = lineDashOffset,
                miterLimit = miterLimit,
                lineCap = lineCap,
                lineJoin = lineJoin,
                fillColor = fillColor,
                font = font,
                transform = transform,
                lineDashPattern = lineDashPattern,
                clipPath = clipPath
            )
        }

        companion object {
            fun create(
                strokeColor: Color = Color.TRANSPARENT,
                strokeWidth: Double = 1.0,
                lineDashOffset: Double = 0.0,
                miterLimit: Double = 10.0,
                lineCap: LineCap = LineCap.BUTT,
                lineJoin: LineJoin = LineJoin.MITER,
                fillColor: Color = Color.TRANSPARENT,
                font: Font = Font(),
                transform: AffineTransform = AffineTransform.IDENTITY,
                lineDashPattern: List<Double>? = null,
                clipPath: Path2d? = null
            ): StateEntry {
                return StateEntry(
                    strokeColor = strokeColor,
                    strokeWidth = strokeWidth,
                    lineDashPattern = lineDashPattern,
                    lineDashOffset = lineDashOffset,
                    miterLimit = miterLimit,
                    lineCap = lineCap,
                    lineJoin = lineJoin,
                    fillColor = fillColor,
                    font = font,
                    transform = transform,
                    clipPath = clipPath
                )
            }
        }
    }

    fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        log { "setTransform(m00=$m00, m10=$m10, m01=$m01, m11=$m11, m02=$m02, m12=$m12)" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        currentState.transform =
            AffineTransform.makeMatrix(m00 = m00, m10 = m10, m01 = m01, m11 = m11, m02 = m02, m12 = m12)
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    private fun transform(at: AffineTransform) {
        log { "transform(${at.repr()})" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        currentState.transform = currentState.transform.concat(at)
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        log { "transform(sx=$sx, ry=$ry, rx=$rx, sy=$sy, tx=$tx, ty=$ty)" }
        transform(AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty))
    }

    fun scale(x: Double, y: Double) {
        log { "scale($x, $y)" }
        transform(AffineTransform.makeScale(x, y))
    }

    fun rotate(angle: Double) {
        log { "rotate($angle)" }
        transform(AffineTransform.makeRotation(angle))
    }

    fun translate(x: Double, y: Double) {
        log { "translate($x, $y)" }
        transform(AffineTransform.makeTranslation(x, y))
    }

    fun save() {
        log { "save([${currentState.transform.repr()}])" }
        states += currentState.copy()
    }

    fun restore() {
        log { "restore()" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        if (states.isNotEmpty()) {
            currentState = states.removeAt(states.size - 1)
        }
        log { "\t  to: [${currentState.transform.repr()}]" }
        log { "\t clipPath: ${currentState.clipPath}" }
    }

    fun beginPath() {
        currentPath = Path2d()
    }

    fun closePath() {
        currentPath.closePath()
    }

    fun clip() {
        log { "clip() - ${currentPath.getCommands()}" }
        currentState.clipPath = currentPath.copy()
    }

    fun moveTo(x: Double, y: Double) {
        val (tx, ty) = currentState.transform.transform(x, y)
        currentPath.moveTo(tx, ty)
    }

    fun lineTo(x: Double, y: Double) {
        val (tx, ty) = currentState.transform.transform(x, y)
        currentPath.lineTo(tx, ty)
    }

    fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean = false
    ) {
        currentPath.arc(
            x = x,
            y = y,
            radiusX = radius,
            radiusY = radius,
            rotation = 0.0,
            startAngle = startAngle,
            endAngle = endAngle,
            anticlockwise = anticlockwise,
            at = currentState.transform
        )
    }

    fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.arc(
            x = x,
            y = y,
            radiusX = radiusX,
            radiusY = radiusY,
            rotation = rotation,
            startAngle = startAngle,
            endAngle = endAngle,
            anticlockwise = anticlockwise,
            at = currentState.transform
        )
    }

    fun bezierCurveTo(
        cp1x: Double,
        cp1y: Double,
        cp2x: Double,
        cp2y: Double,
        x: Double,
        y: Double
    ) {
        currentPath.bezierCurveTo(
            cp1x = cp1x,
            cp1y = cp1y,
            cp2x = cp2x,
            cp2y = cp2y,
            x = x,
            y = y,
            at = currentState.transform
        )
    }

    fun setStrokeStyle(color: Color?) {
        log { "setStrokeStyle($color)" }
        currentState.strokeColor = color ?: Color.TRANSPARENT
    }

    fun setFillStyle(color: Color?) {
        log { "setFillStyle($color)" }
        currentState.fillColor = color ?: Color.TRANSPARENT
    }

    fun setFont(font: Font) {
        log { "setFont($font)" }
        currentState.font = font
    }

    fun setLineWidth(lineWidth: Double) {
        log { "setLineWidth($lineWidth)" }
        currentState.strokeWidth = lineWidth
    }

    fun setLineCap(lineCap: LineCap) {
        log { "setLineCap($lineCap)" }
        currentState.lineCap = lineCap
    }

    fun setLineJoin(lineJoin: LineJoin) {
        log { "setLineJoin($lineJoin)" }
        currentState.lineJoin = lineJoin
    }

    fun setLineDashPattern(lineDashPattern: List<Double>?) {
        log { "setLineDashPattern($lineDashPattern)" }
        currentState.lineDashPattern = lineDashPattern
    }

    fun setLineDashOffset(lineDashOffset: Double) {
        log { "setLineDashOffset($lineDashOffset)" }
        currentState.lineDashOffset = lineDashOffset
    }

    fun setStrokeMiterLimit(miterLimit: Double) {
        log { "setStrokeMiterLimit($miterLimit)" }
        currentState.miterLimit = miterLimit
    }

    fun setGlobalAlpha(d: Double) {
        TODO("Not yet implemented")
    }

    fun scale(d: Double) {
        scale(d, d)
    }

    fun setTextBaseline(baseline: TextBaseline) {
        TODO("Not yet implemented")
    }

    fun setTextAlign(align: TextAlign) {
        TODO("Not yet implemented")
    }
}
