/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package org.jetbrains.letsPlot.pythonExtension.interop

import MagickWand.MagickFalse
import MagickWand.MagickWriteImage
import Python.PyObject
import Python.Py_BuildValue
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotHtmlExport
import org.jetbrains.letsPlot.core.util.PlotHtmlHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.nat.util.PlotSvgExportNative
import org.jetbrains.letsPlot.pythonExtension.interop.TypeUtils.pyDictToMap
import org.jetbrains.letsPlot.raster.builderLW.MonolithicSkiaLW
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

    fun generateSvg(plotSpecDict: CPointer<PyObject>?, useCssPixelatedImageRendering: Int): CPointer<PyObject>? {
        return try {


            val plotSpecMap = pyDictToMap(plotSpecDict)

            run {
                val rawSpec = plotSpecMap as MutableMap<String, Any>
                val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
                val vm = MonolithicSkiaLW.buildPlotFromProcessedSpecs(processedSpec) {}

                val w = 500
                val h = 500
                val canvasControl = MagickCanvasControl(w, h)
                SvgCanvasFigure(vm.svg).mapToCanvas(canvasControl)
                val canvas = canvasControl.children.single() as MagickCanvas

                // Save the image to a file
                val outputFilename = "/home/ikupriyanov/Pictures/imagick_svg_to_raster.bmp"
                if (MagickWriteImage(canvas.wand, outputFilename) == MagickFalse) {
                    println("Failed to save image $outputFilename")
                    throw RuntimeException("Failed to write image")
                } else {
                    println("Image saved to $outputFilename")
                }

            }

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
}