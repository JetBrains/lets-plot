/*
  Copyright (c) 2019. JetBrains s.r.o.
  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
*/
#include <Python.h>

#include "liblets_plot_python_extension_api.h"


#define __ liblets_plot_python_extension_symbols()->
#define T_(name) liblets_plot_python_extension_kref_org_jetbrains_letsPlot_pythonExtension_interop_ ## name

// Note, that as we cache this in the global, and Kotlin/Native object references
// are currently thread local, we make this global a TLS variable.
#ifdef _MSC_VER
#define TLSVAR __declspec(thread)
#else
#define TLSVAR __thread
#endif

// Deprecated: replaced by get_display_html_for_raw_spec() with default parameters
/*
static PyObject* generate_html(PyObject* self, PyObject* rawPlotSpecDict) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    // Create empty dict for sizing options (will default to notebookCell sizing)
    PyObject* emptySizingOptions = PyDict_New();

    // Call generateDisplayHtmlForRawSpec with default parameters
    // dynamicScriptLoading=1 (true), forceImmediateRender=0 (false), responsive=0 (false)
    PyObject* html = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.generateDisplayHtmlForRawSpec(
        reprGen,
        rawPlotSpecDict,
        emptySizingOptions,
        1,  // dynamicScriptLoading = true
        0,  // forceImmediateRender = false
        0   // responsive = false
    );

    Py_DECREF(emptySizingOptions);
    return html;
}
*/

static PyObject* export_svg(PyObject* self, PyObject* args) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    PyObject *rawPlotSpecDict;
    float width;
    float height;
    const char* unit;
    int useCssPixelatedImageRendering;          // 0 - false, 1 - true
    if (!PyArg_ParseTuple(args, "Offsp", &rawPlotSpecDict, &width, &height, &unit, &useCssPixelatedImageRendering)) {
        PyErr_SetString(PyExc_TypeError, "export_svg: failed to parse arguments");
        return NULL;
    }

    //printf("export_svg: width=%f, height=%f, unit=%s, useCssPixelatedImageRendering=%d\n", width, height, unit, useCssPixelatedImageRendering);

    PyObject* svg = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.generateSvg(reprGen, rawPlotSpecDict, width, height, unit, useCssPixelatedImageRendering);
    return svg;
}

static PyObject* export_png(PyObject* self, PyObject* args) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    PyObject *rawPlotSpecDict;
    float width;
    float height;
    const char* unit;
    int dpi;
    float scale;
    if (!PyArg_ParseTuple(args, "Offsif", &rawPlotSpecDict, &width, &height, &unit, &dpi, &scale)) {
        PyErr_SetString(PyExc_TypeError, "export_png: failed to parse arguments");
        return NULL;
    }

    //printf("export_png: width=%f, height=%f, unit=%s, dpi=%d, scale=%f\n", width, height, unit, dpi, scale);

    PyObject* imageData = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.exportPng(reprGen, rawPlotSpecDict, width, height, unit, dpi, scale);
    return imageData; // base64 encoded PNG
}

static PyObject* export_mvg(PyObject* self, PyObject* args) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    PyObject *rawPlotSpecDict;
    float width;
    float height;
    const char* unit;
    int dpi;
    float scale;
    if (!PyArg_ParseTuple(args, "Offsif", &rawPlotSpecDict, &width, &height, &unit, &dpi, &scale)) {
        PyErr_SetString(PyExc_TypeError, "export_mvg: failed to parse arguments");
        return NULL;
    }

    //printf("export_mvg: width=%f, height=%f, unit=%s, dpi=%d, scale=%f\n", width, height, unit, dpi, scale);

    PyObject* mvg = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.exportMvg(reprGen, rawPlotSpecDict, width, height, unit, dpi, scale);
    return mvg;
}

static PyObject* export_html(PyObject* self, PyObject* args) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    // parse arguments
    PyObject *rawPlotSpecDict;
    const char *scriptUrl;
    int iframe;          // 0 - false, 1 - true
    PyArg_ParseTuple(args, "Osp", &rawPlotSpecDict, &scriptUrl, &iframe);

    PyObject* html = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.generateExportHtml(reprGen, rawPlotSpecDict, (void*)scriptUrl, iframe);
    return html;
}

static PyObject* get_static_configure_html(PyObject* self, PyObject* scriptUrl) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    if (!PyUnicode_Check(scriptUrl)) {
        PyErr_SetString(PyExc_TypeError, "string argument expected");
        return NULL;
    }

    const char* scriptUrlStr = PyUnicode_AsUTF8(scriptUrl);
    PyObject* html = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.generateStaticConfigureHtml(reprGen, (void*)scriptUrlStr);
    return html;
}

static PyObject* get_display_html_for_raw_spec(PyObject* self, PyObject* args) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    PyObject *plotSpecDict;
    PyObject *sizingOptionsDict;
    int dynamicScriptLoading;
    int forceImmediateRender;
    int responsive;
    int height100pct;

    PyArg_ParseTuple(args, "OOpppp",
        &plotSpecDict,
        &sizingOptionsDict,
        &dynamicScriptLoading,
        &forceImmediateRender,
        &responsive,
        &height100pct
    );

    PyObject* html = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.generateDisplayHtmlForRawSpec(
        reprGen,
        plotSpecDict,
        sizingOptionsDict,
        dynamicScriptLoading,
        forceImmediateRender,
        responsive,
        height100pct
    );
    return html;
}

static PyObject* get_static_html_page_for_raw_spec(PyObject* self, PyObject* args) {
    T_(PlotReprGenerator) reprGen = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator._instance();

    PyObject *plotSpecDict;
    const char *scriptUrl;
    PyObject *sizingOptionsDict;
    int dynamicScriptLoading;
    int forceImmediateRender;
    int responsive;
    int height100pct;

    PyArg_ParseTuple(args, "OsOpppp",
        &plotSpecDict,
        &scriptUrl,
        &sizingOptionsDict,
        &dynamicScriptLoading,
        &forceImmediateRender,
        &responsive,
        &height100pct
    );

    PyObject* html = __ kotlin.root.org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator.generateStaticHtmlPageForRawSpec(
        reprGen,
        plotSpecDict,
        (void*)scriptUrl,
        sizingOptionsDict,
        dynamicScriptLoading,
        forceImmediateRender,
        responsive,
        height100pct
    );
    return html;
}

static PyMethodDef module_methods[] = {
// { "generate_html", (PyCFunction)generate_html, METH_O, "Generates HTML and JS sufficient for buidling of interactive plot." },  // Deprecated: use get_display_html_for_raw_spec
   { "export_svg", (PyCFunction)export_svg, METH_VARARGS, "Generates SVG representing plot." },
   { "export_html", (PyCFunction)export_html, METH_VARARGS, "Generates HTML page showing plot." },
   { "export_mvg", (PyCFunction)export_mvg, METH_VARARGS, "Generates MVG string representing plot. For internal use." },
   { "export_png", (PyCFunction)export_png, METH_VARARGS, "Generates Base64-encoded PNG string representing plot." },
   { "get_static_configure_html", (PyCFunction)get_static_configure_html, METH_O, "Generates static HTML configuration." },
   { "get_display_html_for_raw_spec", (PyCFunction)get_display_html_for_raw_spec, METH_VARARGS, "Generates display HTML for raw plot spec." },
   { "get_static_html_page_for_raw_spec", (PyCFunction)get_static_html_page_for_raw_spec, METH_VARARGS, "Generates static HTML page for raw plot spec." },
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
