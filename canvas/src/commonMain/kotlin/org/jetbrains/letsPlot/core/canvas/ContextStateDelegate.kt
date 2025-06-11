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
) : Context2d by Context2dDelegate(logEnabled = logEnabled, failIfNotImplemented = failIfNotImplemented) {
    private var stateChangeListener: (stateChange: StateChange) -> Unit = { _ -> }
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

    // Generate StateChange that will transform the initial state to the current state.
    fun restartStateChange() : StateChange {
        val diff = StateChange(
            strokeColor = currentState.strokeColor,
            strokeWidth = currentState.strokeWidth,
            lineDashPattern = currentState.lineDashPattern,
            lineDashOffset = currentState.lineDashOffset,
            miterLimit = currentState.miterLimit,
            lineCap = currentState.lineCap,
            lineJoin = currentState.lineJoin,
            fillColor = currentState.fillColor,
            font = currentState.font,
            fontTextAlign = currentState.fontTextAlign,
            fontBaseline = currentState.fontBaseline,
            transform = AffineTransform.restoreTransform(AffineTransform.IDENTITY, currentState.transform),
            globalAlpha = currentState.globalAlpha,
            clipPath = currentState.clipPath
        )

        return diff
    }

    fun setStateChangeListener(handler: (StateChange) -> Unit) {
        stateChangeListener = handler
    }

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
        val newClipPath = currentPath.copy()
        if (newClipPath != currentState.clipPath) {
            currentState.clipPath = newClipPath
            stateChangeListener(StateChange(clipPath = newClipPath))
        }
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
            val diff = StateChange(
                strokeColor = newState.strokeColor.takeIf { it != currentState.strokeColor },
                strokeWidth = newState.strokeWidth.takeIf { it != currentState.strokeWidth },
                lineDashPattern = newState.lineDashPattern.takeIf { it != currentState.lineDashPattern },
                lineDashOffset = newState.lineDashOffset.takeIf { it != currentState.lineDashOffset },
                miterLimit = newState.miterLimit.takeIf { it != currentState.miterLimit },
                lineCap = newState.lineCap.takeIf { it != currentState.lineCap },
                lineJoin = newState.lineJoin.takeIf { it != currentState.lineJoin },
                fillColor = newState.fillColor.takeIf { it != currentState.fillColor },
                font = newState.font.takeIf { it != currentState.font },
                fontTextAlign = newState.fontTextAlign.takeIf { it != currentState.fontTextAlign },
                fontBaseline = newState.fontBaseline.takeIf { it != currentState.fontBaseline },
                transform = if (newState.transform != currentState.transform) {
                    AffineTransform.restoreTransform(currentState.transform, newState.transform)
                        ?: error { "Cannot restore transform [${currentState.transform.repr()}]." }
                } else {
                    null
                },
                globalAlpha = newState.globalAlpha.takeIf { it != currentState.globalAlpha },
                clipPath = newState.clipPath.takeIf { it != currentState.clipPath },
            )
            currentState = newState
            stateChangeListener(diff)
        }
        log { "\t  to: [${currentState.transform.repr()}]" }
        log { "\t clipPath: ${currentState.clipPath}" }
    }

    override fun setFillStyle(color: Color?) {
        log { "setFillStyle($color)" }
        val newFillStyle = color ?: Color.TRANSPARENT
        if (newFillStyle != currentState.fillColor) {
            currentState.fillColor = newFillStyle
            stateChangeListener(StateChange(fillColor = newFillStyle))
        }
    }

    override fun setStrokeStyle(color: Color?) {
        log { "setStrokeStyle($color)" }
        val newStrokeStyle = color ?: Color.TRANSPARENT
        if (newStrokeStyle != currentState.strokeColor) {
            currentState.strokeColor = newStrokeStyle
            stateChangeListener(StateChange(strokeColor = newStrokeStyle))
        }
    }

    override fun setGlobalAlpha(alpha: Double) {
        log { "setGlobalAlpha($alpha)" }
        currentState.globalAlpha = alpha
    }

    override fun setFont(f: Font) {
        log { "setFont($f)" }
        if (f != currentState.font) {
            currentState.font = f
            stateChangeListener(StateChange(font = f))
        }
    }

    override fun setLineWidth(lineWidth: Double) {
        log { "setLineWidth($lineWidth)" }
        if (lineWidth != currentState.strokeWidth) {
            currentState.strokeWidth = lineWidth
            stateChangeListener(StateChange(strokeWidth = lineWidth))
        }
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        log { "setLineJoin($lineJoin)" }
        if (lineJoin != currentState.lineJoin) {
            currentState.lineJoin = lineJoin
            stateChangeListener(StateChange(lineJoin = lineJoin))
        }
    }

    override fun setLineCap(lineCap: LineCap) {
        log { "setLineCap($lineCap)" }
        if (lineCap != currentState.lineCap) {
            currentState.lineCap = lineCap
            stateChangeListener(StateChange(lineCap = lineCap))
        }
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        log { "setStrokeMiterLimit($miterLimit)" }
        if (miterLimit != currentState.miterLimit) {
            currentState.miterLimit = miterLimit
            stateChangeListener(StateChange(miterLimit = miterLimit))
        }
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        log { "setTextBaseline($baseline)" }
        if (baseline != currentState.fontBaseline) {
            currentState.fontBaseline = baseline
            stateChangeListener(StateChange(fontBaseline = baseline))
        }
    }

    override fun setTextAlign(align: TextAlign) {
        log { "setTextAlign($align)" }
        if (align != currentState.fontTextAlign) {
            currentState.fontTextAlign = align
            stateChangeListener(StateChange(fontTextAlign = align))
        }
    }

    override fun setLineDash(lineDash: DoubleArray) {
        val lineDashPattern = lineDash.toList()
        log { "setLineDashPattern(${lineDashPattern})" }
        if (lineDashPattern != currentState.lineDashPattern) {
            currentState.lineDashPattern = lineDashPattern
            stateChangeListener(StateChange(lineDashPattern = lineDashPattern))
        }
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        log { "setLineDashOffset($lineDashOffset)" }
        if (lineDashOffset != currentState.lineDashOffset) {
            currentState.lineDashOffset = lineDashOffset
            stateChangeListener(StateChange(lineDashOffset = lineDashOffset))
        }
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
        if (newTransform != currentState.transform) {
            log { "\tfrom: [${currentState.transform.repr()}]" }
            log { "\t  to: [${newTransform.repr()}]" }
            currentState.transform = newTransform

            val transform = AffineTransform.restoreTransform(currentState.transform, newTransform)
                ?: error { "Cannot restore transform [${currentState.transform.repr()}]." }

            stateChangeListener(StateChange(transform = transform))
        }
    }

    private fun transform(at: AffineTransform) {
        log { "transform(${at.repr()})" }
        val newTransform = currentState.transform.concat(at)
        if (newTransform != currentState.transform) {
            log { "\tfrom: [${currentState.transform.repr()}]" }
            log { "\t  to: [${newTransform.repr()}]" }
            currentState.transform = newTransform
            stateChangeListener(StateChange(transform = at))
        }
    }

    class StateChange(
        val strokeColor: Color? = null,
        val strokeWidth: Double? = null,
        val lineDashPattern: List<Double>? = null,
        val lineDashOffset: Double? = null,
        val miterLimit: Double? = null,
        val lineCap: LineCap? = null,
        val lineJoin: LineJoin? = null,
        val fillColor: Color? = null,
        val font: Font? = null,
        val fontTextAlign: TextAlign? = null,
        val fontBaseline: TextBaseline? = null,
        val transform: AffineTransform? = null, // delta - should be applied to the current transform
        val globalAlpha: Double? = null,
        val clipPath: Path2d? = null,
    ) {
        override fun toString(): String {
            val str = StringBuilder()
            if (strokeColor != null) str.append("strokeColor=$strokeColor, ")
            if (strokeWidth != null) str.append("strokeWidth=$strokeWidth, ")
            if (lineDashPattern != null) str.append("lineDashPattern=$lineDashPattern, ")
            if (lineDashOffset != null) str.append("lineDashOffset=$lineDashOffset, ")
            if (miterLimit != null) str.append("miterLimit=$miterLimit, ")
            if (lineCap != null) str.append("lineCap=$lineCap, ")
            if (lineJoin != null) str.append("lineJoin=$lineJoin, ")
            if (fillColor != null) str.append("fillColor=$fillColor, ")
            if (font != null) str.append("font=$font, ")
            if (fontTextAlign != null) str.append("fontTextAlign=$fontTextAlign, ")
            if (fontBaseline != null) str.append("fontBaseline=$fontBaseline, ")
            if (transform != null) str.append("transform=${transform.repr()}, ")
            if (globalAlpha != null) str.append("globalAlpha=$globalAlpha, ")
            if (clipPath != null) str.append("clipPath=$clipPath, ")
            if (str.isNotEmpty()) {
                str.setLength(str.length - 2) // remove last ", "
            }
            return "StateChange($str)"
        }
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
