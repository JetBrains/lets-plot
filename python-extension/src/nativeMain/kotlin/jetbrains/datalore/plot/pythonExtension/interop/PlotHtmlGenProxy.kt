package jetbrains.datalore.plot.pythonExtension.interop

import Python.PyObject
import Python.Py_BuildValue
import jetbrains.datalore.plot.pythonExtension.PlotHtmlGen
import jetbrains.datalore.plot.pythonExtension.interop.TypeUtils.pyDictToMap
import kotlinx.cinterop.CPointer

object PlotHtmlGenProxy {
    fun applyToRawSpecs(plotSpecDict: CPointer<PyObject>?): CPointer<PyObject>? {
        val plotSpecMap = pyDictToMap(plotSpecDict)

//        println(plotSpecMap)

        val html = PlotHtmlGen.applyToRawSpecs(plotSpecMap as MutableMap<String, Any>)

        val result = Py_BuildValue("s", html);
        return result
    }
}