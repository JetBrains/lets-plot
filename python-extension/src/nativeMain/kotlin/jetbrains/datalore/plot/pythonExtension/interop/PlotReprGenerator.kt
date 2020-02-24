package jetbrains.datalore.plot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import jetbrains.datalore.plot.PlotHtmlHelper
import jetbrains.datalore.plot.PlotSvgExport
import jetbrains.datalore.plot.pythonExtension.interop.TypeUtils.pyDictToMap
import kotlinx.cinterop.CPointer

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
        val svg = PlotSvgExport.buildSvgImageFromRawSpecs(plotSpecMap as MutableMap<String, Any>)
        val result = Py_BuildValue("s", svg);
        return result
    }
}