import ImageMagick.DrawingWand
import demoAndTestShared.ImageComparer
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasProvider
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import platform.posix.*

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

fun createCanvas(width: Number = 100, height: Number = 100, pixelDensity: Double = 1.0, fontManager: MagickFontManager = MagickFontManager()): Pair<MagickCanvas, Context2d> {
    val canvas = MagickCanvas.create(width = width, height = height, pixelDensity = pixelDensity, fontManager = fontManager)
    val context2d = canvas.context2d
    return canvas to context2d
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

fun getCurrentDir(): String {
    return memScoped {
        val bufferSize = 4096 * 8
        val buffer = allocArray<ByteVar>(bufferSize)
        if (getcwd(buffer, bufferSize.convert()) != null) {
            buffer.toKString()
        } else {
            "." // Default to current directory on error
        }
    }
}

fun writeToFile(path: String, data: ByteArray) {
    if (data.isEmpty()) {
        val file = fopen(path, "wb")
        if (file == null) {
            perror("fopen")
            throw Error("Failed to open file for writing (empty): $path")
        }
        fclose(file)
        return
    }

    val file: CPointer<FILE>? = fopen(path, "wb")
    if (file == null) {
        perror("fopen")
        throw Error("Failed to open file for writing: $path")
    }
    try {
        val written = data.usePinned { pinned ->
            // fwrite(ptr, size_of_element, number_of_elements, stream)
            // It's common to use size 1 and number of elements as the total size.
            fwrite(pinned.addressOf(0), 1.toULong(), data.size.toULong(), file)
        }
        @Suppress("RemoveRedundantCallsOfConversionMethods")
        if (written.toLong() != data.size.toLong()) {
            val errorNum = ferror(file)
            if (errorNum != 0) {
                println("fwrite error: ferror returned $errorNum")
            }
            throw Error("Failed to write all data to file: $path. Wrote $written of ${data.size} bytes.")
        }
    } finally {
        fclose(file)
    }
}
fun readFromFile(path: String): ByteArray {
    val file: CPointer<FILE>? = fopen(path, "rb")
    if (file == null) {
        perror("fopen")
        throw Error("Failed to open file for reading: $path")
    }
    try {
        fseek(file, 0, SEEK_END)
        @Suppress("RemoveRedundantCallsOfConversionMethods")
        val fileSize = ftell(file).toLong()
        if (fileSize < 0L) { // ftell returns -1 on error
            perror("ftell")
            throw Error("Failed to determine file size: $path")
        }
        rewind(file)
        if (fileSize == 0L) {
            return ByteArray(0)
        }
        val buffer = ByteArray(fileSize.toInt())
        val readBytes = buffer.usePinned { pinned ->
            // fread(ptr, size_of_element, number_of_elements, stream)
            fread(pinned.addressOf(0), 1.toULong(), fileSize.toULong(), file)
        }
        @Suppress("RemoveRedundantCallsOfConversionMethods") // on Windows readBytes has type Int
        if (readBytes.toLong() != fileSize) {
            val errorNum = ferror(file)
            if (errorNum != 0) {
                println("fread error: ferror returned $errorNum")
            }
            val atEof = feof(file)
            if (atEof != 0) {
                println("fread error: End-of-file reached prematurely.")
            }
            throw Error("Failed to read all data from file: $path. Read $readBytes of $fileSize bytes.")
        }
        return buffer
    } finally {
        fclose(file)
    }
}

fun imageComparer(): ImageComparer {
    return ImageComparer(
        expectedDir = getCurrentDir() + "/src/nativeTest/resources/expected/",
        outDir = getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider,
        bitmapIO = PngBitmapIO,
        tol = 1
    )
}

fun assertCanvas(expectedFileName: String, canvas: MagickCanvas) {
    imageComparer().assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)
}
