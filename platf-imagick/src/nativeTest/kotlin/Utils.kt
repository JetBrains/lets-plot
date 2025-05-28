import ImageMagick.DrawingWand
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.Context2d

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */



var Context2d.lineWidth: Double
    get() = error("lineWidth is write only")
    set(value) {
        setLineWidth(value)
    }

var Context2d.fillStyle: Any?
    get() = error("fillStyle is write only")
    set(value) {
        val color = when (value) {
            is Color -> value
            is String -> Colors.parseColor(value)
            null -> null
            else -> error("Unsupported fill style: $value")
        }

        setFillStyle(color)
    }

var Context2d.strokeStyle: Any?
    get() = error("strokeStyle is write only")
    set(value) {
        val color = when (value) {
            is Color -> value
            is String -> Colors.parseColor(value)
            null -> null
            else -> error("Unsupported fill style: $value")
        }

        setStrokeStyle(color)
    }

fun Context2d.moveTo(x: Number, y: Number) {
    moveTo(x.toDouble(), y.toDouble())
}

fun Context2d.lineTo(x: Number, y: Number) {
    lineTo(x.toDouble(), y.toDouble())
}

fun Context2d.bezierCurveTo(
    cp1x: Number,
    cp1y: Number,
    cp2x: Number,
    cp2y: Number,
    x: Number,
    y: Number
) {
    bezierCurveTo(
        cp1x.toDouble(),
        cp1y.toDouble(),
        cp2x.toDouble(),
        cp2y.toDouble(),
        x.toDouble(),
        y.toDouble()
    )
}

fun Context2d.ellipse(
    x: Number,
    y: Number,
    radiusX: Number,
    radiusY: Number,
    rotation: Number,
    startAngle: Number,
    endAngle: Number,
    anticlockwise: Boolean = false
) {
    ellipse(
        x.toDouble(),
        y.toDouble(),
        radiusX.toDouble(),
        radiusY.toDouble(),
        rotation.toDouble(),
        startAngle.toDouble(),
        endAngle.toDouble(),
        anticlockwise
    )
}

fun Context2d.translate(x: Number, y: Number) {
    translate(x.toDouble(), y.toDouble())
}

fun Context2d.arc(
    x: Number,
    y: Number,
    radius: Number,
    startAngle: Number,
    endAngle: Number,
    anticlockwise: Boolean = false
) {
    arc(
        x.toDouble(),
        y.toDouble(),
        radius.toDouble(),
        startAngle.toDouble(),
        endAngle.toDouble(),
        anticlockwise
    )
}

fun Context2d.transform(
    sx: Number,
    ry: Number,
    rx: Number,
    sy: Number,
    tx: Number,
    ty: Number
) {
    transform(
        sx.toDouble(),
        ry.toDouble(),
        rx.toDouble(),
        sy.toDouble(),
        tx.toDouble(),
        ty.toDouble()
    )
}

fun Context2d.fillRect(x: Number, y: Number, width: Number, height: Number) {
    fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
}

val black = ImageMagick.NewPixelWand().apply {
    ImageMagick.PixelSetColor(this, "black")
}

val none = ImageMagick.NewPixelWand().apply {
    ImageMagick.PixelSetColor(this, "none")

}

val white = ImageMagick.NewPixelWand().apply {
    ImageMagick.PixelSetColor(this, "white")
}

val green = ImageMagick.NewPixelWand().apply {
    ImageMagick.PixelSetColor(this, "green")
}

val alphaBlack = ImageMagick.NewPixelWand().apply {
    ImageMagick.PixelSetColor(this, "rgba(0,0,0,0.5)")
}

fun defineClipPath(wand: CPointer<DrawingWand>, clipPathId: String, block: () -> Unit) {
    ImageMagick.DrawPushDefs(wand)
    ImageMagick.DrawPushClipPath(wand, clipPathId)
    ImageMagick.PushDrawingWand(wand)

    block()

    ImageMagick.PopDrawingWand(wand)
    ImageMagick.DrawPopClipPath(wand)
    ImageMagick.DrawPopDefs(wand)
}

fun drawAnnotation(wand: CPointer<DrawingWand>, x: Double, y: Double, text: String) {
    memScoped {
        ImageMagick.DrawAnnotation(wand, x, y, text.cstr.ptr.reinterpret())
    }
}

fun drawAffine(
    wand: CPointer<DrawingWand>,
    sx: Number = 1,
    rx: Number = 0,
    ry: Number = 0,
    sy: Number = 1,
    tx: Number = 0,
    ty: Number = 0
) {
    memScoped {
        val m = alloc<ImageMagick.AffineMatrix>()
        m.sx = sx.toDouble()
        m.sy = sy.toDouble()
        m.rx = rx.toDouble()
        m.ry = ry.toDouble()
        m.tx = tx.toDouble()
        m.ty = ty.toDouble()
        ImageMagick.DrawAffine(wand, m.ptr)
    }
}