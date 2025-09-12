/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop

import Python.PyErr_SetString
import Python.PyExc_ValueError
import Python.PyObject
import Python.Py_BuildValue
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.PlotExportCommon.computeExportParameters
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
    fun generateSvg(
        plotSpecDict: CPointer<PyObject>?,
        width: Float,
        height: Float,
        unit: CPointer<ByteVar>,
        useCssPixelatedImageRendering: Int,
    ): CPointer<PyObject>? {
        return try {
            val plotSize = if (width >= 0 && height >= 0) DoubleVector(width, height) else null
            val sizeUnit = SizeUnit.fromName(unit.toKString())

            val plotSpecMap = pyDictToMap(plotSpecDict)

            @Suppress("UNCHECKED_CAST")
            val svg = PlotSvgExportNative.buildSvgImageFromRawSpecs(
                plotSpec = plotSpecMap as MutableMap<String, Any>,
                plotSize = plotSize,
                sizeUnit = sizeUnit,
                useCssPixelatedImageRendering = useCssPixelatedImageRendering == 1,
            )
            Py_BuildValue("s", svg)
        } catch (e: Throwable) {
            val svgStr = """
                <svg style="width:100%;height:100%;" xmlns="http://www.w3.org/2000/svg">
                    <text x="0" y="20">generateSvg() - Exception: ${e.message}</text>
                </svg>
            """.trimIndent()
            println(e.message)
            e.printStackTrace()
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

    // Returns bitmap and dpi or null on error
    fun exportBitmap(
        plotSpec: Map<*, *>,
        fontManager: MagickFontManager,
        plotSize: DoubleVector? = null,
        sizeUnit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = null,
    ): Pair<Bitmap, Double> {
        var canvasReg: Registration? = null
        try {
            val exportParameters = computeExportParameters(plotSize, dpi, sizeUnit, scale)

            @Suppress("UNCHECKED_CAST")
            val plotCanvasFigure = MonolithicCanvas.buildPlotFigureFromRawSpec(
                rawSpec = plotSpec as MutableMap<String, Any>,
                sizingPolicy = exportParameters.sizingPolicy,
                computationMessagesHandler = { }
            )

            val canvasControl = MagickCanvasControl(
                w = plotCanvasFigure.bounds().get().width,
                h = plotCanvasFigure.bounds().get().height,
                pixelDensity = exportParameters.scaleFactor,
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
            canvasControl.dispose()

            return bitmap to exportParameters.dpi
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
        try {
            val plotSize = if (width >= 0 && height >= 0) DoubleVector(width, height) else null
            val sizeUnit = SizeUnit.fromName(unit.toKString())
            val dpi = if (dpi >= 0) dpi.toDouble() else null
            val scaleFactor = if (scale >= 0) scale.toDouble() else null

            val (bitmap, bitmapDpi) = exportBitmap(
                plotSpec = pyDictToMap(plotSpecDict),
                plotSize = plotSize,
                sizeUnit = sizeUnit,
                dpi = dpi,
                scale = scaleFactor,
                fontManager = defaultFontManager
            )
            // We can't use PyBytes_FromStringAndSize(ptr, bytes.size.toLong()):
            // Type mismatch: inferred type is CPointer<ByteVarOf<Byte>>? but String? was expected
            // This happens because PyBytes_FromStringAndSize has the following signature:
            // PyObject *PyBytes_FromStringAndSize(const char *v, Py_ssize_t len);
            // Here `const char*` refers to a pointer to a byte buffer. Kotlin cinterop fails to infer that
            // and generate a function with a String parameter instead of ByteArray

            val png: ByteArray = Png.encode(bitmap, bitmapDpi)
            return Py_BuildValue("s", Base64.encode(png))
        } catch (e: Throwable) {
            e.printStackTrace()
            // Set a Python exception with the caught error message
            PyErr_SetString(PyExc_ValueError, "${e.message}")
            // Return null to signal that an exception was raised
            return null
        }
    }
}
