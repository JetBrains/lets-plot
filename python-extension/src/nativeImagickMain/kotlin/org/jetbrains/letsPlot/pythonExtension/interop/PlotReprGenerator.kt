/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotHtmlExport
import org.jetbrains.letsPlot.core.util.PlotHtmlHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.nat.util.PlotSvgExportNative
import org.jetbrains.letsPlot.pythonExtension.interop.TypeUtils.pyDictToMap
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure

object PlotReprGenerator {
    fun generateDynamicDisplayHtml(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlHelper.getDynamicDisplayHtmlForRawSpec(plotSpecMap as MutableMap<String, Any>)
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateDynamicDisplayHtml() - Exception: ${e.message}");
        }
    }

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

    fun generateSvg(plotSpecDict: CPointer<PyObject>?, useCssPixelatedImageRendering: Int): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)

            @Suppress("UNCHECKED_CAST")
            val svg = PlotSvgExportNative.buildSvgImageFromRawSpecs(
                plotSpec = plotSpecMap as MutableMap<String, Any>,
                plotSize = null,
                useCssPixelatedImageRendering = useCssPixelatedImageRendering == 1,
            )
            Py_BuildValue("s", svg)
        } catch (e: Throwable) {
            val svgStr = """
                <svg style="width:100%;height:100%;" xmlns="http://www.w3.org/2000/svg">
                    <text x="0" y="20">generateSvg() - Exception: ${e.message}</text>
                </svg>
            """.trimIndent()
            Py_BuildValue("s", svgStr);
        }
    }

    fun generateStaticHtmlPage(
        plotSpecDict: CPointer<PyObject>?,
        scriptUrlCStr: CPointer<ByteVar>,
        iFrame: Int
    ): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)
            val scriptUrl = scriptUrlCStr.toKString()

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlExport.buildHtmlFromRawSpecs(
                plotSpec = plotSpecMap as MutableMap<String, Any>,
                scriptUrl = scriptUrl,
                iFrame = iFrame == 1
            )
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateStaticHtmlPage() - Exception: ${e.message}");
        }
    }

    fun generateStaticConfigureHtml(
        scriptUrlCStr: CPointer<ByteVar>,
    ): CPointer<PyObject>? {
        return try {
            val scriptUrl = scriptUrlCStr.toKString()
            val html = PlotHtmlHelper.getStaticConfigureHtml(scriptUrl)
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateStaticConfigureHtml() - Exception: ${e.message}");
        }
    }

    fun generateDisplayHtmlForRawSpec(
        plotSpecDict: CPointer<PyObject>,
        sizingOptionsDict: CPointer<PyObject>,
        dynamicScriptLoading: Int,
        forceImmediateRender: Int,
        responsive: Int
    ): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)
            val sizingOptionsMap = pyDictToMap(sizingOptionsDict)
            val sizingPolicy = SizingPolicy.create(sizingOptionsMap)

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlHelper.getDisplayHtmlForRawSpec(
                plotSpec = plotSpecMap as MutableMap<String, Any>,
                sizingPolicy = sizingPolicy,
                dynamicScriptLoading = dynamicScriptLoading == 1,
                forceImmediateRender = forceImmediateRender == 1,
                responsive = responsive == 1,
                removeComputationMessages = false,
                logComputationMessages = false
            )
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateDisplayHtmlForRawSpec() - Exception: ${e.message}")
        }
    }

    fun saveImage(
        plotSpecDict: CPointer<PyObject>?,
        filePath: CPointer<ByteVar>,
        dpi: Int,
        width: Int,
        height: Int,
        scale: Float
    ): CPointer<PyObject>? {
        var canvasReg: Registration? = null

        try {
            @Suppress("UNCHECKED_CAST")
            val processedSpec = MonolithicCommon.processRawSpecs(
                plotSpec = pyDictToMap(plotSpecDict) as MutableMap<String, Any>,
                frontendOnly = false
            )

            val vm = MonolithicCanvas.buildPlotFromProcessedSpecs(
                plotSpec = processedSpec,
                computationMessagesHandler = { println(it.joinToString("\n")) }
            )

            val svgCanvasFigure = SvgCanvasFigure(vm.svg)

            val canvasControl = MagickCanvasControl(
                w = svgCanvasFigure.width,
                h = svgCanvasFigure.height,
                pixelDensity = 1.0
            )

            canvasReg = svgCanvasFigure.mapToCanvas(canvasControl)

            // TODO: canvasControl can provide takeSnapshot() method
            val plotCanvas = canvasControl.children.single() as MagickCanvas

            // Save the image to a file
            plotCanvas.saveBmp(filePath.toKString())
            val outputFilePath = filePath.toKString()
            return Py_BuildValue("s", outputFilePath)
            //if (ImageMagick.MagickWriteImage(plotCanvas.img, outputFilePath) == ImageMagick.MagickFalse) {
            //    println("Failed to save image $outputFilePath")
            //    println(getMagickError(plotCanvas.img))
            //    throw RuntimeException("Failed to write image: $outputFilePath\n${getMagickError(plotCanvas.img)}")
            //} else {
            //    println("Image saved to $outputFilePath")
            //    return Py_BuildValue("s", outputFilePath)
            //}
        } catch (e: Throwable) {
            return null
        } finally {
            canvasReg?.dispose()
        }
    }
}
