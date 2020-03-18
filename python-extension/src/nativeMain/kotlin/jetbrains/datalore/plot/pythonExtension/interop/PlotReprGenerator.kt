package jetbrains.datalore.plot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import jetbrains.datalore.plot.PlotHtmlExport
import jetbrains.datalore.plot.PlotHtmlHelper
import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.datalore.plot.pythonExtension.interop.TypeUtils.pyDictToMap
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString

object PlotReprGenerator {
    fun generateDynamicDisplayHtml(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        val plotSpecMap = pyDictToMap(plotSpecDict)

        @Suppress("UNCHECKED_CAST")
        val html = PlotHtmlHelper.getDynamicDisplayHtmlForRawSpec(plotSpecMap as MutableMap<String, Any>)
        val result = Py_BuildValue("s", html);
        return result
    }

    fun generateSvg(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        val plotSpecMap = pyDictToMap(plotSpecDict)

        @Suppress("UNCHECKED_CAST")
        val svg = PlotSvgExportPortable.buildSvgImageFromRawSpecs(plotSpecMap as MutableMap<String, Any>)
        val result = Py_BuildValue("s", svg);
        return result
    }

    fun generateStaticHtmlPage(
        plotSpecDict: CPointer<PyObject>?,
        versionCStr: CPointer<ByteVar>
    ): CPointer<PyObject>? {
        val plotSpecMap = pyDictToMap(plotSpecDict)
        val version = versionCStr.toKString()

        // ToDo: iFrame
        @Suppress("UNCHECKED_CAST")
        val html = PlotHtmlExport.buildHtmlFromRawSpecs(plotSpecMap as MutableMap<String, Any>, version)
        val result = Py_BuildValue("s", html);
        return result
    }
}