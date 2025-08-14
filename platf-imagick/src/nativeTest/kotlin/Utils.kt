
import ImageMagick.DrawingWand
import demoAndTestShared.ImageComparer
import demoAndTestShared.NativeBitmapIO
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager.FontSet
import org.jetbrains.letsPlot.imagick.canvas.MagickSnapshot
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

fun Context2d.setFont(family: String, size: Number, style: FontStyle = FontStyle.NORMAL, weight: FontWeight = FontWeight.NORMAL) {
    val font = Font(
        fontFamily = family,
        fontSize = size.toDouble(),
        fontStyle = style,
        fontWeight = weight
    )
    setFont(font)
}

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

val resourcesDir = Native.getCurrentDir() + "/src/nativeTest/resources/"

val notoSerifRegularFontPath = resourcesDir + "fonts/NotoSerif-Regular.ttf"
val notoSerifBoldFontPath = resourcesDir + "fonts/NotoSerif-Bold.ttf"
val notoSerifItalicFontPath = resourcesDir + "fonts/NotoSerif-Italic.ttf"
val notoSerifBoldItalicFontPath = resourcesDir + "fonts/NotoSerif-BoldItalic.ttf"

val notoSansMonoRegularFontPath = resourcesDir + "fonts/NotoSansMono-Regular.ttf"
val notoSansMonoBoldFontPath = resourcesDir + "fonts/NotoSansMono-Bold.ttf"


fun embeddedFontsManager() = MagickFontManager.configured(
    "serif" to FontSet(embedded = true, familyName = "serif", notoSerifRegularFontPath)
)

fun createImageComparer(fontManager: MagickFontManager): ImageComparer {
    return ImageComparer(
        expectedDir = Native.getCurrentDir() + "/src/nativeTest/resources/expected/",
        outDir = Native.getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider(fontManager),
        bitmapIO = NativeBitmapIO,
        tol = 1
    )
}

class MagickCanvasProvider(
    private val magickFontManager: MagickFontManager,
) : CanvasProvider {
    override fun createCanvas(size: Vector): MagickCanvas {
        return MagickCanvas.create(size.x, size.y, 1.0, magickFontManager)
    }

    override fun createSnapshot(bitmap: Bitmap): MagickSnapshot {
        return MagickSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        println("MagickCanvasControl.createSnapshot(dataUrl): dataUrl.size = ${dataUrl.length}")
        val bitmap = Png.decodeDataImage(dataUrl)
        return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        val img = MagickUtil.fromBitmap(Png.decode(png))
        return Asyncs.constant(MagickSnapshot(img))
    }
}