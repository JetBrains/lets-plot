package jetbrains.datalore.visualization.plot.pythonExtension

import Python.*
import jetbrains.datalore.visualization.plot.pythonExtension.interop.PlotHtmlGenProxy
import kotlinx.cinterop.*

val scope = MemScope()

fun createMethods(): CArrayPointer<PyMethodDef> {
    val methodDefs = nativeHeap.allocArray<PyMethodDef>(2)

    cValue<PyMethodDef> {
        ml_name = "generate_html".cstr.getPointer(scope)
        ml_flags = METH_VARARGS
        ml_meth = staticCFunction { _, args -> PlotHtmlGenProxy.applyToRawSpecs(args)}
        ml_doc = "Generates HTML representing plot".cstr.getPointer(scope)
    }.place(methodDefs[0].ptr)
    cValue<PyMethodDef> {
        ml_name = null
        ml_flags = 0
        ml_meth = null
        ml_doc = null
    }.place(methodDefs[1].ptr)


    return methodDefs
}

val module = cValue<PyModuleDef> {
    m_name = "datalore_plot_kotlin_extension".cstr.getPointer(scope)
    m_doc = "".cstr.getPointer(scope)
    m_size = -1
    m_methods = createMethods()
}

@CName("PyInit_libdatalore_plot_python_extension")
fun PyInit_libdatalore_plot_python_extension() = PyModule_Create2(module, PYTHON_API_VERSION)