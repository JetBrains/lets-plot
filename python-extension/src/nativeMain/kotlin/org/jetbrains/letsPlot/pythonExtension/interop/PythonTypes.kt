/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

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
