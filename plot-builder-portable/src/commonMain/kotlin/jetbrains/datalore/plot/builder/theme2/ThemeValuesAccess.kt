/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem

open class ThemeValuesAccess(
    private val values: Map<String, Any>
) {
    private val mem: MutableMap<String, Any> = HashMap()

    protected operator fun get(option: String): Any? = values[option]
    protected fun getValue(option: String): Any = values.getValue(option)

//    protected fun getElemValue(option: String): Map<String, Any> {
//        @Suppress("UNCHECKED_CAST")
//        return getValue(option) as Map<String, Any>
//    }

    /**
     * @param options A stack of option manes: the most specific - first.
     */
    protected fun getElemValue(options: List<String>): Map<String, Any> {
        val specificOption = options.first()
        @Suppress("UNCHECKED_CAST")
        return mem.getOrPut(specificOption) {
            return options.asReversed().map { values[it] }
                .fold(HashMap<String, Any>()) { acc, v ->
                    if (v != null) {
                        acc.putAll(v as Map<String, Any>)
                    }
                    acc
                }
        } as Map<String, Any>
    }

//    protected fun isElemBlank(option: String): Boolean {
//        val blankValue = getElemValue(option)[Elem.BLANK]
//        return blankValue != null && blankValue as Boolean
//    }

    protected fun isElemBlank(options: List<String>): Boolean {
        val blankValue = getElemValue(options)[Elem.BLANK]
        return blankValue != null && blankValue as Boolean
    }

    protected fun getNumber(elem: Map<String, Any>, option: String): Double {
        return (elem.getValue(option) as Number).toDouble()
    }
}