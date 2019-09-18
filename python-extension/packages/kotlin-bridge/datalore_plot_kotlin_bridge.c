#include <Python.h>

#include "libdatalore_plot_python_extension_api.h"


#define __ libdatalore_plot_python_extension_symbols()->
#define T_(name) libdatalore_plot_python_extension_kref_jetbrains_datalore_visualization_plot_pythonExtension_ ## name

// Note, that as we cache this in the global, and Kotlin/Native object references
// are currently thread local, we make this global a TLS variable.
#ifdef _MSC_VER
#define TLSVAR __declspec(thread)
#else
#define TLSVAR __thread
#endif

//static PyObject* ping(PyObject* self) {
//    PyObject* result = Py_BuildValue("s", "Hello from datalore_plot_kotlin_bridge");
//    return result;
//}

static PyObject* generate_html(PyObject* self) {
    T_(SampleBarPlot) sampleBarPlot = __ kotlin.root.jetbrains.datalore.visualization.plot.pythonExtension.SampleBarPlot.SampleBarPlot();
    const char* html = __ kotlin.root.jetbrains.datalore.visualization.plot.pythonExtension.SampleBarPlot.getHTML(sampleBarPlot);
    __ DisposeStablePointer(sampleBarPlot.pinned);


//    PyObject* result = Py_BuildValue("s", "Hello from datalore_plot_kotlin_bridge.generate_html()");
    PyObject* result = Py_BuildValue("s", html);
    return result;
}


static PyMethodDef module_methods[] = {
//   { "ping", (PyCFunction)ping, METH_NOARGS, "Just for test" },
   { "generate_html", (PyCFunction)generate_html, METH_NOARGS, "Show sample plot" },
   { NULL }
};


static struct PyModuleDef module_def = {
        PyModuleDef_HEAD_INIT,
        "datalore_plot_kotlin_bridge",
        NULL,
        -1,     // m_size: -1 => module does not support sub-interpreters, has global state
        module_methods,
        NULL,   // m_slots: using single-phase initialization
        NULL,   // m_traverse
        NULL,   // m_clear
        NULL    // m_free
};

PyMODINIT_FUNC PyInit_datalore_plot_kotlin_bridge(void) {
   PyObject *module = PyModule_Create(&module_def);
   return module;
}
