package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer

class Context2dDelegate : Context2d {
    override fun clearRect(rect: DoubleRectangle) { }
    override fun drawImage(snapshot: Canvas.Snapshot) { }
    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) { }
    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) { }
    override fun drawImage(snapshot: Canvas.Snapshot, sx: Double, sy: Double, sw: Double, sh: Double, dx: Double, dy: Double, dw: Double, dh: Double) { }
    override fun beginPath() { }
    override fun closePath() { }
    override fun stroke() { }
    override fun fill() { }
    override fun fillEvenOdd() { }
    override fun fillRect(x: Double, y: Double, w: Double, h: Double) { }
    override fun moveTo(x: Double, y: Double) { }
    override fun lineTo(x: Double, y: Double) { }
    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) { }
    override fun save() { }
    override fun restore() { }
    override fun setFillStyle(color: Color?) { }
    override fun setStrokeStyle(color: Color?) { }
    override fun setGlobalAlpha(alpha: Double) { }
    override fun setFont(f: Font) { }
    override fun setLineWidth(lineWidth: Double) { }
    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) { }
    override fun strokeText(text: String, x: Double, y: Double) { }
    override fun fillText(text: String, x: Double, y: Double) { }
    override fun scale(x: Double, y: Double) { }
    override fun scale(xy: Double) { }
    override fun rotate(angle: Double) { }
    override fun translate(x: Double, y: Double) { }
    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) { }
    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) { }
    override fun setLineJoin(lineJoin: LineJoin) { }
    override fun setLineCap(lineCap: LineCap) { }
    override fun setTextBaseline(baseline: TextBaseline) { }
    override fun setTextAlign(align: TextAlign) { }
    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) { }
    override fun setLineDash(lineDash: DoubleArray) { }
    override fun measureText(str: String): Double { return 0.0}
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
