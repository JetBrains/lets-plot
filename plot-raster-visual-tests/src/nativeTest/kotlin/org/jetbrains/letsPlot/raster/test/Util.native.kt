@file:OptIn(ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.ImageComparer
import demoAndTestShared.NativeBitmapIO
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.util.PlotExportCommon
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.imagick.canvas.MagickSnapshot
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil
import org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator

val fontsDir = Native.getCurrentDir() + "/src/nativeTest/resources/fonts/"

fun newEmbeddedFontsManager() = MagickFontManager.configured(
    "sans" to MagickFontManager.FontSet(embedded = true, "NotoSans",
        regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
        italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
        boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
    ),
    "sans-serif" to MagickFontManager.FontSet(embedded = true, "NotoSans",
        regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
        italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
        boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
    ),
    "serif" to MagickFontManager.FontSet(embedded = true, "NotoSerif",
        regularFontPath = "$fontsDir/NotoSerif-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSerif-Bold.ttf",
        italicFontPath = "$fontsDir/NotoSerif-Italic.ttf",
        boldItalicFontPath = "$fontsDir/NotoSerif-BoldItalic.ttf"
    ),
    "mono" to MagickFontManager.FontSet(embedded = true, "NotoSansMono",
        regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSansMono-Bold.ttf"
    ),
    "regular_mono" to MagickFontManager.FontSet(embedded = true, "NotoSansMono",
        regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf"
    ),
    "oblique_bold" to MagickFontManager.FontSet(embedded = true, "Noto",
        regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
        obliqueFontPath = "$fontsDir/NotoSans-Italic.ttf",
        boldObliqueFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
    ),
    "oblique" to MagickFontManager.FontSet(embedded = true, "Noto",
        regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
        obliqueFontPath = "$fontsDir/NotoSans-Italic.ttf"
    ),
)


fun createImageComparer(): ImageComparer {
    return ImageComparer(
        canvasProvider = MagickCanvasProvider(MagickFontManager.default()),
        bitmapIO = NativeBitmapIO,
        expectedDir = Native.getCurrentDir() + "/src/nativeTest/resources/expected/",
        outDir = Native.getCurrentDir() + "/build/reports/",
    )
}

actual fun assertPlot(
    fonts: List<String>,
    expectedFileName: String,
    plotSpec: MutableMap<String, Any>,
    width: Number?,
    height: Number?,
    unit: PlotExportCommon.SizeUnit?,
    dpi: Number?,
    scale: Number?
) {
    val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

    val (bitmap, _) = PlotReprGenerator.exportBitmap(
        plotSpec = plotSpec,
        plotSize = plotSize,
        sizeUnit = unit,
        dpi = dpi,
        scale = scale,
        fontManager = newEmbeddedFontsManager()
        //fontManager = MagickFontManager.default() // For manual testing
    )

    val imageComparer: ImageComparer = createImageComparer()
    imageComparer.assertBitmapEquals(expectedFileName, bitmap)
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
