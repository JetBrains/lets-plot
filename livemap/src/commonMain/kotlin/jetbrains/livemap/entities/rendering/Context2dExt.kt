package jetbrains.livemap.entities.rendering

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.Context2d

object Context2dExt {
    fun Context2d.drawImage(snapshot: Canvas.Snapshot, p: DoubleVector) = drawImage(snapshot, p.x, p.y)
    fun Context2d.drawImage(snapshot: Canvas.Snapshot, origin: DoubleVector, dimension: DoubleVector) {
        drawImage(snapshot, origin.x, origin.y, dimension.x, dimension.y)
    }
    fun Context2d.drawImage(
        snapshot: Canvas.Snapshot,
        srcCoord: DoubleVector,
        srcSize: DoubleVector,
        dstCoord: DoubleVector,
        dstSize: DoubleVector
    ) {
        this.drawImage(
            snapshot = snapshot,
            sx = srcCoord.x,
            sy = srcCoord.y,
            sw = srcSize.x,
            sh = srcSize.y,
            dx = dstCoord.x,
            dy = dstCoord.y,
            dw = dstSize.x,
            dh = dstSize.y
        )
    }

    fun Context2d.drawImage(
        snapshot: Canvas.Snapshot,
        src: DoubleRectangle,
        dst: DoubleRectangle
    ) {
        this.drawImage(
            snapshot = snapshot,
            sx = src.origin.x,
            sy = src.origin.y,
            sw = src.dimension.x,
            sh = src.dimension.y,
            dx = dst.origin.x,
            dy = dst.origin.y,
            dw = dst.dimension.x,
            dh = dst.dimension.y
        )
    }

    fun Context2d.fillRect(origin: DoubleVector, dimension: DoubleVector) {
        fillRect(origin.x, origin.y, dimension.x, dimension.y)
    }
    fun Context2d.strokeRect(origin: DoubleVector, dimension: DoubleVector) {
        strokeRect(origin.x, origin.y, dimension.x, dimension.y)
    }

    fun Context2d.fillRect(rect: DoubleRectangle) = fillRect(rect.origin, rect.dimension)
    fun Context2d.strokeRect(rect: DoubleRectangle) = strokeRect(rect.origin, rect.dimension)
    fun Context2d.moveTo(p: DoubleVector) = moveTo(p.x, p.y)
    fun Context2d.lineTo(p: DoubleVector) = lineTo(p.x, p.y)
    fun Context2d.strokeText(str: String, p: DoubleVector) = strokeText(str, p.x, p.y)
    fun Context2d.fillText(str: String, p: DoubleVector) = fillText(str, p.x, p.y)
    fun Context2d.scale(scale: DoubleVector) = scale(scale.x, scale.y)
    fun Context2d.translate(p: DoubleVector) = translate(p.x, p.y)

}