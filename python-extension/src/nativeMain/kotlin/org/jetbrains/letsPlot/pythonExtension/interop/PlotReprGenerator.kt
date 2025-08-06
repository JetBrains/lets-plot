/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.util.PlotHtmlExport
import org.jetbrains.letsPlot.core.util.PlotHtmlHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.nat.util.PlotSvgExportNative
import org.jetbrains.letsPlot.pythonExtension.interop.TypeUtils.pyDictToMap
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas

object PlotReprGenerator {
    private val defaultFontManager by lazy { MagickFontManager.default() }

    @Suppress("unused") // This function is used in kotlin_bridge.c
    fun generateDynamicDisplayHtml(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlHelper.getDynamicDisplayHtmlForRawSpec(plotSpecMap as MutableMap<String, Any>)
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateDynamicDisplayHtml() - Exception: ${e.message}")
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
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
            Py_BuildValue("s", svgStr)
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
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
            Py_BuildValue("s", "generateStaticHtmlPage() - Exception: ${e.message}")
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
    fun generateStaticConfigureHtml(
        scriptUrlCStr: CPointer<ByteVar>,
    ): CPointer<PyObject>? {
        return try {
            val scriptUrl = scriptUrlCStr.toKString()
            val html = PlotHtmlHelper.getStaticConfigureHtml(scriptUrl)
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateStaticConfigureHtml() - Exception: ${e.message}")
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
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

    fun exportBitmap(
        plotSpec: Map<*, *>,
        width: Float,
        height: Float,
        unit: String,
        dpi: Int,
        scale: Double,
        fontManager: MagickFontManager
    ): Bitmap? {
        var canvasReg: Registration? = null

        try {
            val sizingPolicy = when {
                width < 0 || height < 0 -> SizingPolicy.keepFigureDefaultSize()
                else -> {
                    // Build the plot in logical pixels (always 96 DPI) and then render it scaled.
                    // Otherwise, the plot will be rendered incorrectly, i.e., with too many tick labels and small font sizes.
                    val (logicalWidth, logicalHeight) = when (unit) {
                        "cm" -> (width * 96 / 2.54) to (height * 96 / 2.54)
                        "in" -> (width * 96) to (height * 96)
                        "mm" -> (width * 96 / 25.4) to (height * 96 / 25.4)
                        "px" -> width to height
                        else -> throw IllegalArgumentException("Unsupported unit: $unit")
                    }

                    SizingPolicy.fixed(
                        width = logicalWidth.toDouble(),
                        height = logicalHeight.toDouble()
                    )
                }
            }

            @Suppress("UNCHECKED_CAST")
            val plotCanvasFigure = MonolithicCanvas.buildPlotFigureFromRawSpec(
                rawSpec = plotSpec as MutableMap<String, Any>,
                sizingPolicy = sizingPolicy,
                computationMessagesHandler = {
                    //println(it.joinToString("\n"))
                }
            )

            val scaleFactor = when {
                dpi > 0 -> dpi / 96.0 * scale
                else -> scale
            }

            val canvasControl = MagickCanvasControl(
                w = plotCanvasFigure.bounds().get().width,
                h = plotCanvasFigure.bounds().get().height,
                pixelDensity = scaleFactor,
                fontManager = fontManager,
            )

            canvasReg = plotCanvasFigure.mapToCanvas(canvasControl)

            // TODO: canvasControl can provide takeSnapshot() method
            val plotCanvas = canvasControl.children.last() as MagickCanvas
            require(plotCanvas.size.x > 0 && plotCanvas.size.y > 0) {
                "Plot canvas size must be greater than zero"
            }

            // Save the image to a file
            val snapshot = plotCanvas.takeSnapshot()
            val bitmap = snapshot.bitmap
            snapshot.dispose()
            return bitmap
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        } finally {
            canvasReg?.dispose()
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
    fun exportPng(
        plotSpecDict: CPointer<PyObject>?,
        width: Float,
        height: Float,
        unit: CPointer<ByteVar>,
        dpi: Int,
        scale: Float
    ): CPointer<PyObject>? {
        val bitmap = exportBitmap(
            plotSpec = pyDictToMap(plotSpecDict),
            width = width,
            height = height,
            unit = unit.toKString(),
            dpi = dpi,
            scale = scale.toDouble(),
            fontManager = defaultFontManager
        ) ?: return Py_BuildValue("s", "Failed to generate image")
        // We can't use PyBytes_FromStringAndSize(ptr, bytes.size.toLong()):
        // Type mismatch: inferred type is CPointer<ByteVarOf<Byte>>? but String? was expected
        // This happens because PyBytes_FromStringAndSize has the following signature:
        // PyObject *PyBytes_FromStringAndSize(const char *v, Py_ssize_t len);
        // Here `const char*` refers to a pointer to a byte buffer. Kotlin cinterop fails to infer that
        // and generate a function with a String parameter instead of ByteArray

        val png: ByteArray = Png.encode(bitmap)
        return Py_BuildValue("s", Base64.encode(png))
    }
}
