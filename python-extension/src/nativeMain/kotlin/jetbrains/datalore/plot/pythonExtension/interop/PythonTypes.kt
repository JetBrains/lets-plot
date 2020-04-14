package jetbrains.datalore.plot.pythonExtension.interop

import Python.PyObject_Type
import Python.PyTypeObject
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString

internal object PythonTypes {
    fun getPyObjectType(obj: TPyObjPtr): String? {
        val objType = PyObject_Type(obj)
        if (objType == null) {
            return null
        }

        return objType.reinterpret<PyTypeObject>().pointed.tp_name?.toKString()
    }

    const val STR = "str"
    const val INT = "int"
    const val FLOAT = "float"
    const val BOOL = "bool"
    const val NONE = "NoneType"
    const val LIST = "list"
    const val DICT = "dict"
    const val TUPLE = "tuple"
}
