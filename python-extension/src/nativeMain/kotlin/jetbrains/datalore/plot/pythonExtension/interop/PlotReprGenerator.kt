package jetbrains.datalore.plot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import org.jetbrains.letsPlot.core.util.PlotHtmlExport
import org.jetbrains.letsPlot.core.util.PlotHtmlHelper
import jetbrains.datalore.plot.pythonExtension.interop.TypeUtils.pyDictToMap
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.nat.util.PlotSvgExportNative

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
}