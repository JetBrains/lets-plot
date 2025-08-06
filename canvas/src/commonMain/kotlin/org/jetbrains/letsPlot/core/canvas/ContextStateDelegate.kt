/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.values.Color

private const val logEnabled = false
private fun log(str: () -> String) {
    if (logEnabled)
        println(str())
}

class ContextStateDelegate(
    logEnabled: Boolean = true,
    failIfNotImplemented: Boolean = true
) : Context2d by Context2dDelegate(
    logEnabled = logEnabled,
    failIfNotImplemented = failIfNotImplemented
) {
    private val states = ArrayList<StateEntry>()
    private var currentPath: Path2d = Path2d()
    private var currentState = StateEntry(
        fillColor = Color.TRANSPARENT,
        strokeColor = Color.TRANSPARENT,
        strokeWidth = 1.0,
        lineDashPattern = emptyList(),
        lineDashOffset = 0.0,
        miterLimit = 10.0,
        lineCap = LineCap.BUTT,
        lineJoin = LineJoin.MITER,
        font = Font(),
        fontTextAlign = TextAlign.START,
        fontBaseline = TextBaseline.ALPHABETIC,
        globalAlpha = 1.0,
        transform = AffineTransform.IDENTITY,
        clipPath = Path2d()
    )

    fun getCTM(): AffineTransform {
        return currentState.transform
    }

    fun getCurrentPath(): List<Path2d.PathCommand> {
        return currentPath.getCommands()
    }

    fun getClipPath(): Path2d {
        return currentState.clipPath
    }

    fun getLineDash(): List<Double> {
        return currentState.lineDashPattern
    }

    fun getLineDashOffset(): Double {
        return currentState.lineDashOffset
    }

    fun getFont(): Font {
        return currentState.font
    }

    override fun beginPath() {
        currentPath = Path2d()
    }

    override fun moveTo(x: Double, y: Double) {
        val (tx, ty) = currentState.transform.transform(x, y)
        currentPath.moveTo(tx, ty)
    }

    override fun lineTo(x: Double, y: Double) {
        val (tx, ty) = currentState.transform.transform(x, y)
        currentPath.lineTo(tx, ty)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
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

    override fun bezierCurveTo(
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

    override fun closePath() {
        currentPath.closePath()
    }

    override fun clip() {
        log { "clip() - ${currentPath.getCommands()}" }
        currentState.clipPath = currentPath.copy()
    }

    override fun save() {
        log { "save([${currentState.transform.repr()}])" }
        states += currentState.copy()
    }

    override fun restore() {
        log { "restore()" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        if (states.isNotEmpty()) {
            val newState = states.removeAt(states.size - 1)
            currentState = newState
        }
        log { "\t  to: [${currentState.transform.repr()}]" }
        log { "\t clipPath: ${currentState.clipPath}" }
    }

    override fun setFillStyle(color: Color?) {
        log { "setFillStyle($color)" }
        currentState.fillColor = color ?: Color.TRANSPARENT
    }

    override fun setStrokeStyle(color: Color?) {
        log { "setStrokeStyle($color)" }
        currentState.strokeColor = color ?: Color.TRANSPARENT
    }

    override fun setGlobalAlpha(alpha: Double) {
        log { "setGlobalAlpha($alpha)" }
        currentState.globalAlpha = alpha
    }

    override fun setFont(f: Font) {
        log { "setFont($f)" }
        currentState.font = f
    }

    override fun setLineWidth(lineWidth: Double) {
        log { "setLineWidth($lineWidth)" }
        currentState.strokeWidth = lineWidth
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        log { "setLineJoin($lineJoin)" }
        currentState.lineJoin = lineJoin
    }

    override fun setLineCap(lineCap: LineCap) {
        log { "setLineCap($lineCap)" }
        currentState.lineCap = lineCap
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        log { "setStrokeMiterLimit($miterLimit)" }
        currentState.miterLimit = miterLimit
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        log { "setTextBaseline($baseline)" }
        currentState.fontBaseline = baseline
    }

    override fun setTextAlign(align: TextAlign) {
        log { "setTextAlign($align)" }
        currentState.fontTextAlign = align
    }

    override fun setLineDash(lineDash: DoubleArray) {
        val lineDashPattern = lineDash.toList()
        log { "setLineDashPattern(${lineDashPattern})" }
        currentState.lineDashPattern = lineDashPattern
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        log { "setLineDashOffset($lineDashOffset)" }
        currentState.lineDashOffset = lineDashOffset
    }

    override fun scale(x: Double, y: Double) {
        log { "scale($x, $y)" }
        transform(AffineTransform.makeScale(x, y))
    }

    override fun scale(xy: Double) {
        scale(xy, xy)
    }

    override fun rotate(angle: Double) {
        log { "rotate($angle)" }
        transform(AffineTransform.makeRotation(angle))
    }

    override fun translate(x: Double, y: Double) {
        log { "translate($x, $y)" }
        transform(AffineTransform.makeTranslation(x, y))
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        log { "transform(sx=$sx, ry=$ry, rx=$rx, sy=$sy, tx=$tx, ty=$ty)" }
        transform(AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty))
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        log { "setTransform(m00=$m00, m10=$m10, m01=$m01, m11=$m11, m02=$m02, m12=$m12)" }
        val newTransform = AffineTransform.makeMatrix(m00 = m00, m10 = m10, m01 = m01, m11 = m11, m02 = m02, m12 = m12)
        log { "\tfrom: [${currentState.transform.repr()}]" }
        log { "\t  to: [${newTransform.repr()}]" }
        currentState.transform = newTransform
    }

    private fun transform(at: AffineTransform) {
        log { "transform(${at.repr()})" }
        val newTransform = currentState.transform.concat(at)
        log { "\tfrom: [${currentState.transform.repr()}]" }
        log { "\t  to: [${newTransform.repr()}]" }
        currentState.transform = newTransform
    }

    private data class StateEntry(
        var strokeColor: Color,
        var strokeWidth: Double,
        var lineDashPattern: List<Double>,
        var lineDashOffset: Double,
        var miterLimit: Double,
        var lineCap: LineCap,
        var lineJoin: LineJoin,
        var fillColor: Color,
        var font: Font,
        var fontTextAlign: TextAlign,
        var fontBaseline: TextBaseline,
        var transform: AffineTransform,
        var globalAlpha: Double,
        var clipPath: Path2d,
    )
}
