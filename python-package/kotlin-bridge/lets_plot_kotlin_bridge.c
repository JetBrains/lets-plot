/*
  Copyright (c) 2019. JetBrains s.r.o.
  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
*/
#include <Python.h>

#include "liblets_plot_python_extension_api.h"


#define __ liblets_plot_python_extension_symbols()->
#define T_(name) liblets_plot_python_extension_kref_jetbrains_datalore_plot_pythonExtension_interop_ ## name

// Note, that as we cache this in the global, and Kotlin/Native object references
// are currently thread local, we make this global a TLS variable.
#ifdef _MSC_VER
#define TLSVAR __declspec(thread)
#else
#define TLSVAR __thread
#endif

static PyObject* generate_html(PyObject* self, PyObject* plotSpecDict) {
    T_(PlotHtmlGenProxy) htmlGen = __ kotlin.root.jetbrains.datalore.plot.pythonExtension.interop.PlotHtmlGenProxy._instance();
    PyObject* html = __ kotlin.root.jetbrains.datalore.plot.pythonExtension.interop.PlotHtmlGenProxy.applyToRawSpecs(htmlGen, plotSpecDict);
    return html;
}

static PyMethodDef module_methods[] = {
   { "generate_html", (PyCFunction)generate_html, METH_O, "Generates HTML representing plot" },
   { NULL }
};


static struct PyModuleDef module_def = {
        PyModuleDef_HEAD_INIT,
        "lets_plot_kotlin_bridge",
        NULL,
        -1,     // m_size: -1 => module does not support sub-interpreters, has global state
        module_methods,
        NULL,   // m_slots: using single-phase initialization
        NULL,   // m_traverse
        NULL,   // m_clear
        NULL    // m_free
};

PyMODINIT_FUNC PyInit_lets_plot_kotlin_bridge(void) {
   PyObject *module = PyModule_Create(&module_def);
   return module;
}
