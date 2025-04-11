/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer

class Context2dDelegate(
    private val logEnabled: Boolean = false,
    private val failIfNotImplemented: Boolean = false
) : Context2d {
    private fun log(msg: String) {
        if (logEnabled) {
            println(msg)
        }

        if (failIfNotImplemented) {
            throw UnsupportedOperationException(msg)
        }
    }

    override fun clearRect(rect: DoubleRectangle) { log("clearRect: $rect") }
    override fun drawImage(snapshot: Canvas.Snapshot) { log("drawImage: $snapshot") }
    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) { log("drawImage: $snapshot, x=$x, y=$y") }
    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) { log("drawImage: $snapshot, x=$x, y=$y, dw=$dw, dh=$dh") }
    override fun drawImage(snapshot: Canvas.Snapshot, sx: Double, sy: Double, sw: Double, sh: Double, dx: Double, dy: Double, dw: Double, dh: Double) { log("drawImage: $snapshot, sx=$sx, sy=$sy, sw=$sw, sh=$sh, dx=$dx, dy=$dy, dw=$dw, dh=$dh") }
    override fun beginPath() { log("beginPath") }
    override fun closePath() { log("closePath") }
    override fun stroke() { log("stroke") }
    override fun fill() { log("fill") }
    override fun fillEvenOdd() { log("fillEvenOdd") }
    override fun fillRect(x: Double, y: Double, w: Double, h: Double) { log("fillRect: x=$x, y=$y, w=$w, h=$h") }
    override fun moveTo(x: Double, y: Double) { log("moveTo: x=$x, y=$y") }
    override fun lineTo(x: Double, y: Double) { log("lineTo: x=$x, y=$y") }
    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) { log("arc: x=$x, y=$y, radius=$radius, startAngle=$startAngle, endAngle=$endAngle, anticlockwise=$anticlockwise") }
    override fun ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) { log("ellipse: x=$x, y=$y, radiusX=$radiusX, radiusY=$radiusY, rotation=$rotation, startAngle=$startAngle, endAngle=$endAngle, anticlockwise=$anticlockwise") }
    override fun save() { log("save") }
    override fun restore() { log("restore") }
    override fun setFillStyle(color: Color?) { log("setFillStyle: $color") }
    override fun setStrokeStyle(color: Color?) { log("setStrokeStyle: $color") }
    override fun setGlobalAlpha(alpha: Double) { log("setGlobalAlpha: $alpha") }
    override fun setFont(f: Font) { log("setFont: $f") }
    override fun setLineWidth(lineWidth: Double) { log("setLineWidth: $lineWidth") }
    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) { log("strokeRect: x=$x, y=$y, w=$w, h=$h") }
    override fun strokeText(text: String, x: Double, y: Double) { log("strokeText: $text, x=$x, y=$y") }
    override fun fillText(text: String, x: Double, y: Double) { log("fillText: $text, x=$x, y=$y") }
    override fun scale(x: Double, y: Double) { log("scale: x=$x, y=$y") }
    override fun scale(xy: Double) { log("scale: xy=$xy") }
    override fun rotate(angle: Double) { log("rotate: angle=$angle") }
    override fun translate(x: Double, y: Double) { log("translate: x=$x, y=$y") }
    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) { log("transform: m11=$sx, m12=$ry, m21=$rx, m22=$sy, dx=$tx, dy=$ty") }
    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) { log("bezierCurveTo: cp1x=$cp1x, cp1y=$cp1y, cp2x=$cp2x, cp2y=$cp2y, x=$x, y=$y") }
    override fun setLineJoin(lineJoin: LineJoin) { log("setLineJoin: $lineJoin") }
    override fun setLineCap(lineCap: LineCap) { log("setLineCap: $lineCap") }
    override fun setStrokeMiterLimit(miterLimit: Double) { log("setStrokeMiterLimit: $miterLimit") }
    override fun setTextBaseline(baseline: TextBaseline) { log("setTextBaseline: $baseline") }
    override fun setTextAlign(align: TextAlign) { log("setTextAlign: $align") }
    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) { log("setTransform: m11=$m00, m12=$m10, m21=$m01, m22=$m11, dx=$m02, dy=$m12") }
    override fun setLineDash(lineDash: DoubleArray) { log("setLineDash: $lineDash") }
    override fun setLineDashOffset(lineDashOffset: Double) { log("setLineDashOffset: $lineDashOffset") }
    override fun measureTextWidth(str: String): Double {
        log("measureTextWidth: '$str'")
        return str.length * 8.0
    }

    override fun measureText(str: String): TextMetrics {
        log("measureText: '$str'")
        return TextMetrics(0.0, 0.0, DoubleRectangle.LTRB(0, 0, str.length * 8.0, 14.0))
    }
}

class CanvasDelegate(
    width: Int, height: Int
) : Canvas {
    override val context2d: Context2d = Context2dDelegate()
    override val size: Vector = Vector(width, height)
    override fun takeSnapshot(): Async<Canvas.Snapshot> = Asyncs.constant(NullSnapshot)
    override fun immidiateSnapshot(): Canvas.Snapshot = NullSnapshot
}

object NullSnapshot : Canvas.Snapshot {
    override fun copy(): Canvas.Snapshot = this
}

open class CanvasControlDelegate(
    width: Int,
    height: Int,
) : CanvasControl {
    override val size: Vector = Vector(width, height)
    override fun addChild(canvas: Canvas) {}
    override fun addChild(index: Int, canvas: Canvas) {}
    override fun removeChild(canvas: Canvas) {}
    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : AnimationTimer {
            override fun start() {}
            override fun stop() {}
        }
    }

    override fun createCanvas(size: Vector): Canvas = CanvasDelegate(size.x, size.y)
    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> = Asyncs.constant(NullSnapshot)
    override fun createSnapshot(bytes: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        return Asyncs.constant(NullSnapshot)
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return Registration.EMPTY
    }

    override fun <T> schedule(f: () -> T) {
        f()
    }
}
