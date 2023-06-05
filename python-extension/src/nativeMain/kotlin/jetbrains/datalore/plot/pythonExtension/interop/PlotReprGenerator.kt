package jetbrains.datalore.plot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import jetbrains.datalore.plot.PlotHtmlExport
import jetbrains.datalore.plot.PlotHtmlHelper
import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.datalore.plot.pythonExtension.interop.TypeUtils.pyDictToMap
import jetbrains.datalore.vis.svgToString.SvgToString
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import pngj.RGBEncoderNative

object PlotReprGenerator {
    fun generateDynamicDisplayHtml(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        try {
            val plotSpecMap = pyDictToMap(plotSpecDict)

            @Suppress("UNCHECKED_CAST")
            val html = PlotHtmlHelper.getDynamicDisplayHtmlForRawSpec(plotSpecMap as MutableMap<String, Any>)
            val result = Py_BuildValue("s", html);
            return result
        } catch (e: Throwable) {
            return Py_BuildValue("s", "generateDynamicDisplayHtml() - Exception: ${e.message}");
        }
    }

    fun generateSvg(plotSpecDict: CPointer<PyObject>?, useCssPixelatedImageRendering: Int): CPointer<PyObject>? {
        try {
            val plotSpecMap = pyDictToMap(plotSpecDict)
            val nativeEncoder = SvgToString(RGBEncoderNative(), useCssPixelatedImageRendering == 1)
            @Suppress("UNCHECKED_CAST")
            val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(plotSpecMap as MutableMap<String, Any>, svgToString = nativeEncoder)
            val result = Py_BuildValue("s", svg);
            return result
        } catch (e: Throwable) {
            return Py_BuildValue("s", "generateSvg() - Exception: ${e.message}");
        }
    }

    fun generateStaticHtmlPage(
        plotSpecDict: CPointer<PyObject>?,
        scriptUrlCStr: CPointer<ByteVar>,
        iFrame: Int
    ): CPointer<PyObject>? {
        try {
            val plotSpecMap = pyDictToMap(plotSpecDict)
            val scriptUrl = scriptUrlCStr.toKString()

            @Suppress("UNCHECKED_CAST")
            val html =
                PlotHtmlExport.buildHtmlFromRawSpecs(plotSpecMap as MutableMap<String, Any>, scriptUrl, iFrame == 1)
            val result = Py_BuildValue("s", html);
            return result
        } catch (e: Throwable) {
            return Py_BuildValue("s", "generateStaticHtmlPage() - Exception: ${e.message}");
        }
    }
}