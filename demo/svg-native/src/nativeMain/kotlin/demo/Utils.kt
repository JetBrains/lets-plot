/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


@OptIn(ExperimentalForeignApi::class)
fun savePlot(plotSpec: MutableMap<String, Any>, filePath: String) {
    var canvasReg: Registration? = null

    try {
        val plotFigure = MonolithicCanvas.buildPlotFigureFromRawSpec(
            rawSpec = plotSpec,
            sizingPolicy = SizingPolicy.keepFigureDefaultSize(),
            computationMessagesHandler = { println(it.joinToString("\n")) }
        )

        println("Plot figure: width=${plotFigure.plotWidth}, height=${plotFigure.plotHeight}")
        val canvasControl = MagickCanvasControl(
            w = plotFigure.plotWidth,
            h = plotFigure.plotHeight,
            pixelDensity = 1.0
        )

        canvasReg = plotFigure.mapToCanvas(canvasControl)

        // TODO: canvasControl can provide takeSnapshot() method
        val plotCanvas = canvasControl.children.single() as MagickCanvas

        // Save the image to a file
        val outputFilePath = filePath
        plotCanvas.saveBmp(outputFilePath)
        println("Image saved to $outputFilePath")
    } finally {
        canvasReg?.dispose()
    }

}

@OptIn(ExperimentalForeignApi::class)
fun getMagickError(wand: CPointer<ImageMagick.MagickWand>?): String {
    require(wand != null) { "MagickWand is null" }

    return memScoped {
        val severity = alloc<ImageMagick.ExceptionTypeVar>()
        val messagePtr = ImageMagick.MagickGetException(wand, severity.ptr)

        if (messagePtr != null) {
            val errorMessage = messagePtr.toKString()
            ImageMagick.MagickRelinquishMemory(messagePtr)
            "ImageMagick Error: $errorMessage"
        } else {
            "Unknown ImageMagick error"
        }
    }
}
