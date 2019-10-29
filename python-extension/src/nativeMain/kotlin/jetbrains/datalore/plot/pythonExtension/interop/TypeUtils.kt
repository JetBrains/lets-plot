package jetbrains.datalore.plot.pythonExtension.interop

import Python.*
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.BOOL
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.DICT
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.FLOAT
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.INT
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.LIST
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.NONE
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.STR
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.TUPLE
import kotlinx.cinterop.*

typealias TPyObjPtr = CPointer<PyObject>

internal object TypeUtils {
    private fun getPyObjectType(obj: TPyObjPtr?) =
        PyObject_Type(obj)!!.reinterpret<PyTypeObject>().pointed.tp_name?.toKString()

    private fun pyObjectToKotlin(obj: TPyObjPtr?): Any? {
        if (obj == null) return null;

        val objType = getPyObjectType(obj)

        return when (objType) {
            STR -> pyStrToString(obj)
            INT -> pyIntToLong(obj)
            FLOAT -> pyFloatToDouble(obj)
            BOOL -> pyBoolToBoolean(obj)
            NONE -> null
            LIST -> pyListToList(obj)
            DICT -> pyDictToMap(obj)
            TUPLE -> pyTupleToList(obj)
            else -> throw IllegalArgumentException("Wrong python type: $objType")
        }
    }

    private fun pyStrToString(str: TPyObjPtr) = PyBytes_AsString(PyUnicode_AsUTF8String(str))?.toKString()

    private fun pyIntToLong(int: TPyObjPtr): Long = PyLong_AsLong(int)

    private fun pyFloatToDouble(float: TPyObjPtr): Double = PyFloat_AsDouble(float)

    private fun pyBoolToBoolean(bool: TPyObjPtr): Boolean = PyObject_IsTrue(bool) == 1

    private fun pyListToList(list: TPyObjPtr): List<Any?> {
        return asSequence(list, ::PyList_Size, ::PyList_GetItem)
            .map(::pyObjectToKotlin)
            .toMutableList()
    }

    private fun pyTupleToList(tuple: TPyObjPtr): List<Any?> {
        return asSequence(tuple, ::PyTuple_Size, ::PyTuple_GetItem)
            .map(::pyObjectToKotlin)
            .toMutableList()
    }

    fun pyDictToMap(dict: TPyObjPtr?): MutableMap<String, Any?> {
        if (dict == null) {
            return mutableMapOf()
        }

        return asSequence(PyDict_Keys(dict)!!, ::PyList_Size, ::PyList_GetItem)
            .associate { key -> pyStrToString(key!!)!! to pyObjectToKotlin(PyDict_GetItem(dict, key)) }
            .toMutableMap()
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