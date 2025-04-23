/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


@OptIn(ExperimentalForeignApi::class)
fun savePlot(plotSpec: MutableMap<String, Any>, filePath: String) {
    var canvasReg: Registration? = null

    try {
        val processedSpec = MonolithicCommon.processRawSpecs(
            plotSpec = plotSpec,
            frontendOnly = false
        )

        val vm = MonolithicCanvas.buildPlotFromProcessedSpecs(
            plotSpec = processedSpec,
            computationMessagesHandler = { println(it.joinToString("\n")) }
        )

        val svgCanvasFigure = SvgCanvasFigure(vm.svg)

        val canvasControl = MagickCanvasControl(
            w = svgCanvasFigure.width,
            h = svgCanvasFigure.height
        )

        canvasReg = svgCanvasFigure.mapToCanvas(canvasControl)

        // TODO: canvasControl can provide takeSnapshot() method
        val plotCanvas = canvasControl.children.single() as MagickCanvas

        // Save the image to a file
        val outputFilePath = filePath
        if (ImageMagick.MagickWriteImage(plotCanvas.wand, outputFilePath) == ImageMagick.MagickFalse) {
            println("Failed to save image $outputFilePath")
            println(getMagickError(plotCanvas.wand))
            throw RuntimeException("Failed to write image: $outputFilePath\n${getMagickError(plotCanvas.wand)}")
        } else {
            println("Image saved to $outputFilePath")
        }
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
