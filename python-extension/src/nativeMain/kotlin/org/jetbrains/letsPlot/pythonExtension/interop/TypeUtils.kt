/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import Python.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.BOOL
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.DICT
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.FLOAT
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.INT
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.LIST
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.NONE
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.STR
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.TUPLE
import org.jetbrains.letsPlot.pythonExtension.interop.PythonTypes.getPyObjectType

typealias TPyObjPtr = CPointer<PyObject>

/**
 * WARNING: Using of Python API functions on wrong Python objects (like calling PyDict_Keys on a str)
 * will cause SEGFAULT and an interpreter crash (with Jupyter kernel) with no chance to prevent it.
 */
internal object TypeUtils {

    fun pyDictToMap(dict: TPyObjPtr?): MutableMap<Any?, Any?> {
        if (dict == null) {
            return mutableMapOf()
        }

        require(getPyObjectType(dict) == DICT) { "pyDictToMap() - unexpceted type: ${getPyObjectType(dict)}" }

        return asSequence(PyDict_Keys(dict)!!, ::PyList_Size, ::PyList_GetItem)
            .associate { key -> pyObjectToKotlin(key!!) to pyObjectToKotlin(PyDict_GetItem(dict, key)) }
            .toMutableMap()
    }

    private fun pyObjectToKotlin(obj: TPyObjPtr?): Any? {
        if (obj == null) return null;

        val objType = getPyObjectType(obj)

        return when (objType) {
            STR -> PyBytes_AsString(PyUnicode_AsUTF8String(obj))?.toKString()
            INT -> PyLong_AsLongLong(obj).toLong()
            FLOAT -> PyFloat_AsDouble(obj)
            BOOL -> PyObject_IsTrue(obj) == 1
            DICT -> pyDictToMap(obj)
            LIST -> asSequence(obj, ::PyList_Size, ::PyList_GetItem).map(TypeUtils::pyObjectToKotlin).toMutableList()
            TUPLE -> asSequence(obj, ::PyTuple_Size, ::PyTuple_GetItem).map(TypeUtils::pyObjectToKotlin).toMutableList()
            NONE -> null
            else -> error("pyObjectToKotlin() - unexpected type: $objType")
        }
    }

    private fun asSequence(
        self: TPyObjPtr,
        getCount: (TPyObjPtr) -> Long,
        getItem: (CValuesRef<PyObject>?, Py_ssize_t) -> TPyObjPtr?
    ): Sequence<TPyObjPtr?> {
        return PyIterator(self, getCount, getItem).asSequence()
    }

    private class PyIterator(
        private val self: TPyObjPtr,
        private val getCount: (TPyObjPtr) -> Long,
        private val getItem: (CValuesRef<PyObject>?, Py_ssize_t) -> TPyObjPtr?
    ) : AbstractIterator<TPyObjPtr?>() {
        private val count = getCount(self)
        private var i = 0L
        override fun computeNext() {
            if (i < count) {
                setNext(getItem(self, i++))
            } else {
                done()
            }
        }
    }
}
