package jetbrains.datalore.plot.pythonExtension.interop

import Python.*
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.BOOL
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.DICT
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.FLOAT
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.INT
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.LIST
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.NONE
import jetbrains.datalore.plot.pythonExtension.interop.PythonTypes.STR
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlin.collections.set

internal object TypeUtils {
    fun getPyObjectType(obj: CPointer<PyObject>?) =
        PyObject_Type(obj)!!.reinterpret<PyTypeObject>().pointed.tp_name?.toKString()

    fun pyObjectToKotlin(obj: CPointer<PyObject>?): Any? {
        if (obj == null) return null;

        val objType = getPyObjectType(obj)

        return when (objType) {
            // ToDo: tuple ?
            STR -> pyStrToString(obj)
            INT -> pyIntToLong(obj)
            FLOAT -> pyFloatToDouble(obj)
            BOOL -> pyBoolToBoolean(obj)
            NONE -> null
            LIST -> pyListToList(obj)
            DICT -> pyDictToMap(obj)
            else -> throw IllegalArgumentException("Wrong python type: $objType")
        }
    }

    fun pyStrToString(str: CPointer<PyObject>) = PyBytes_AsString(PyUnicode_AsUTF8String(str))?.toKString()

    fun pyIntToLong(int: CPointer<PyObject>): Long = PyLong_AsLong(int)

    fun pyFloatToDouble(float: CPointer<PyObject>): Double = PyFloat_AsDouble(float)

    fun pyBoolToBoolean(bool: CPointer<PyObject>): Boolean = PyObject_IsTrue(bool) == 1

    fun pyListToList(list: CPointer<PyObject>): List<Any?> {
        val result = mutableListOf<Any?>()

        val listLen = PyList_Size(list)

        for (i in 0 until listLen) {
            val value = PyList_GetItem(list, i)
            result.add(pyObjectToKotlin(value))
        }

        return result
    }

    fun pyDictToMap(dict: CPointer<PyObject>?): MutableMap<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        val keys = PyDict_Keys(dict)
        val keysLen = PyList_Size(keys)
        for (i in 0 until keysLen) {
            val key = PyList_GetItem(keys, i)
            val keyType = getPyObjectType(key)
            assert(keyType != null && keyType == PythonTypes.STR)
            val keyStr = pyStrToString(key!!)!!

            val value = PyDict_GetItem(dict, key)
            result[keyStr] = pyObjectToKotlin(value)
        }

        return result
    }
}