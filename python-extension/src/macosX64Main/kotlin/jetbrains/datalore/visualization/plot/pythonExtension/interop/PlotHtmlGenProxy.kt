package jetbrains.datalore.visualization.plot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import jetbrains.datalore.visualization.plot.pythonExtension.PlotHtmlGen
import jetbrains.datalore.visualization.plot.pythonExtension.interop.TypeUtils.pyDictToMap
import kotlinx.cinterop.CPointer

object PlotHtmlGenProxy {
    fun applyToRawSpecs(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        val plotSpecMap = pyDictToMap(plotSpecDict)

        val html = PlotHtmlGen.applyToRawSpecs(plotSpecMap as MutableMap<String, Any>)
        println(plotSpecMap)

        val result = Py_BuildValue("s", html);
        return result
    }
}