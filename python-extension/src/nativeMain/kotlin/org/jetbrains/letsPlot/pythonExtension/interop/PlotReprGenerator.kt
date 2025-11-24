/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop

import ImageMagick.DrawGetVectorGraphics
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
import org.jetbrains.letsPlot.core.util.DisplayHtmlPolicy
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.PlotExportCommon.computeExportParameters
import org.jetbrains.letsPlot.core.util.PlotHtmlExport
import org.jetbrains.letsPlot.core.util.PlotHtmlHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.nat.util.PlotSvgExportNative
import org.jetbrains.letsPlot.pythonExtension.interop.TypeUtils.pyDictToMap
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure2
import kotlin.time.TimeSource

object PlotReprGenerator {
    private val defaultFontManager by lazy { MagickFontManager.default() }

    // Deprecated: replaced by generateDisplayHtmlForRawSpec() with default parameters
    // Used to be called from kotlin_bridge.c generate_html() function
//    @Suppress("unused") // This function is used in kotlin_bridge.c
//    fun generateDynamicDisplayHtml(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
//        return try {
//            val plotSpecMap = pyDictToMap(plotSpecDict)
//
//            @Suppress("UNCHECKED_CAST")
//            val html = PlotHtmlHelper.getDisplayHtmlForRawSpec(
//                plotSpec = plotSpecMap as MutableMap<String, Any>,
//                sizingPolicy = SizingPolicy.notebookCell(),
//                dynamicScriptLoading = true,
//                forceImmediateRender = false,
//                responsive = false,
//                removeComputationMessages = false,
//                logComputationMessages = false
//            )
//            Py_BuildValue("s", html)
//        } catch (e: Throwable) {
//            Py_BuildValue("s", "generateDynamicDisplayHtml() - Exception: ${e.message}")
//        }
//    }

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
            //println(e.message)
            //e.printStackTrace()
            Py_BuildValue("s", svgStr)
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
    fun generateExportHtml(
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
            Py_BuildValue("s", "generateExportHtml() - Exception: ${e.message}")
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
        responsive: Int,
        height100pct: Int,
    ): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)
            val sizingOptionsMap = pyDictToMap(sizingOptionsDict)
            val sizingPolicy = SizingPolicy.create(sizingOptionsMap)
            val displayHtmlPolicy = DisplayHtmlPolicy(
                dynamicScriptLoading = dynamicScriptLoading == 1,
                forceImmediateRender = forceImmediateRender == 1,
                responsive = responsive == 1,
                height100pct = height100pct == 1,
            )

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlHelper.getDisplayHtmlForRawSpec(
                plotSpec = plotSpecMap as MutableMap<String, Any>,
                sizingPolicy = sizingPolicy,
                displayHtmlPolicy = displayHtmlPolicy,
                removeComputationMessages = false,
                logComputationMessages = false
            )
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateDisplayHtmlForRawSpec() - Exception: ${e.message}")
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
    fun generateStaticHtmlPageForRawSpec(
        plotSpecDict: CPointer<PyObject>,
        scriptUrlCStr: CPointer<ByteVar>,
        sizingOptionsDict: CPointer<PyObject>,
        dynamicScriptLoading: Int,
        forceImmediateRender: Int,
        responsive: Int,
        height100pct: Int,
    ): CPointer<PyObject>? {
        return try {
            val plotSpecMap = pyDictToMap(plotSpecDict)
            val scriptUrl = scriptUrlCStr.toKString()
            val sizingOptionsMap = pyDictToMap(sizingOptionsDict)
            val sizingPolicy = SizingPolicy.create(sizingOptionsMap)
            val displayHtmlPolicy = DisplayHtmlPolicy(
                dynamicScriptLoading = dynamicScriptLoading == 1,
                forceImmediateRender = forceImmediateRender == 1,
                responsive = responsive == 1,
                height100pct = height100pct == 1,
            )

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlHelper.getStaticHtmlPageForRawSpec(
                plotSpec = plotSpecMap as MutableMap<String, Any>,
                scriptUrl = scriptUrl,
                sizingPolicy = sizingPolicy,
                displayHtmlPolicy = displayHtmlPolicy,
                removeComputationMessages = false,
                logComputationMessages = false
            )
            Py_BuildValue("s", html)
        } catch (e: Throwable) {
            Py_BuildValue("s", "generateStaticHtmlPageForRawSpec() - Exception: ${e.message}")
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
            val rawPlotSpec = plotSpec as MutableMap<String, Any>

            val plotCanvasFigure = PlotCanvasFigure2()
            plotCanvasFigure.update(
                processedSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false),
                sizingPolicy = exportParameters.sizingPolicy,
                computationMessagesHandler = { }
            )

            val magickCanvasPeer = MagickCanvasPeer(
                pixelDensity = exportParameters.scaleFactor,
                fontManager = fontManager,
            )

            canvasReg = plotCanvasFigure.mapToCanvas(magickCanvasPeer)

            val canvas = magickCanvasPeer.createCanvas(plotCanvasFigure.size)

            plotCanvasFigure.paint(canvas.context2d)

            // Save the image to a file
            val snapshot = canvas.takeSnapshot()
            val bitmap = snapshot.bitmap

            canvas.dispose()
            snapshot.dispose()
            magickCanvasPeer.dispose()

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
            //e.printStackTrace()

            // Set a Python exception with the caught error message
            PyErr_SetString(PyExc_ValueError, "${e.message}")
            // Return null to signal that an exception was raised
            return null
        }
    }

    @Suppress("unused") // This function is used in kotlin_bridge.c
    fun exportMvg(
        plotSpecDict: CPointer<PyObject>?,
        width: Float,
        height: Float,
        unit: CPointer<ByteVar>,
        dpi: Int,
        scale: Float
    ): CPointer<PyObject>? {
        var canvasReg: Registration? = null
        try {
            val start = TimeSource.Monotonic.markNow()

            val plotSize = if (width >= 0 && height >= 0) DoubleVector(width, height) else null
            val sizeUnit = SizeUnit.fromName(unit.toKString())
            val dpi = if (dpi >= 0) dpi.toDouble() else null
            val scaleFactor = if (scale >= 0) scale.toDouble() else null
            val plotSpec = pyDictToMap(plotSpecDict)

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): plotSpec parsed")

            val exportParameters = computeExportParameters(plotSize, dpi, sizeUnit, scaleFactor)

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): $exportParameters")

            @Suppress("UNCHECKED_CAST")
            val rawPlotSpec = plotSpec as MutableMap<String, Any>

            val plotCanvasFigure = PlotCanvasFigure2()
            plotCanvasFigure.update(
                processedSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false),
                sizingPolicy = exportParameters.sizingPolicy,
                computationMessagesHandler = { }
            )

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): plotCanvasFigure built, size=${plotCanvasFigure.size}")

            val magickCanvasPeer = MagickCanvasPeer(
                pixelDensity = exportParameters.scaleFactor,
                fontManager = defaultFontManager,
            )

            canvasReg = plotCanvasFigure.mapToCanvas(magickCanvasPeer)

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): plot mapped to canvas")

            val canvas = magickCanvasPeer.createCanvas(plotCanvasFigure.size)

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): canvas size: ${canvas.size}, pixelDensity=${magickCanvasPeer.pixelDensity}")

            plotCanvasFigure.paint(canvas.context2d)

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): plot painted")

            // Save the image to a file
            val snapshot = canvas.takeSnapshot()

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): snapshot taken")

            val bitmap = snapshot.bitmap

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): bitmap extracted")

            val wand = canvas.context2d.wand
            val mvg = DrawGetVectorGraphics(wand)?.toKString() ?: "MagicWand: MVG is null"

            canvas.dispose()
            snapshot.dispose()
            magickCanvasPeer.dispose()

            println("${TimeSource.Monotonic.markNow() - start}: exportMvg(): resources disposed")

            return Py_BuildValue("s", mvg)
        } catch (e: Throwable) {
            canvasReg?.dispose()

            //e.printStackTrace()

            // Set a Python exception with the caught error message
            PyErr_SetString(PyExc_ValueError, "${e.message}")
            // Return null to signal that an exception was raised
            return null
        }
    }

}
