/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem

internal open class ThemeValuesAccess(
    private val values: Map<String, Any>
) {
    private val mem: MutableMap<String, Any> = HashMap()

    protected operator fun get(key: String): Any? = values[key]
    protected fun getValue(key: String): Any = values.getValue(key)

    /**
     * @param key List of option names: the most specific - first.
     */
    private fun getValue(key: List<String>): Any {
        val specificOption = key.first()
        return mem.getOrPut(specificOption) {
            return key.firstNotNullOfOrNull { values[it] }
                ?: throw IllegalStateException("No theme value found. Key : $key.")
        }
    }

    protected fun getNumber(key: List<String>): Double {
        val value = getValue(key)
        return (value as? Number)?.toDouble()
            ?: throw IllegalStateException("Theme value is not a number: $value. Key : $key.")
    }

    protected fun getBoolean(key: List<String>): Boolean {
        val value = getValue(key)
        return (value as? Boolean)
            ?: throw IllegalStateException("Theme value is not boolean: $value. Key : $key.")
    }

    /**
     * @param key List of option names: the most specific - first.
     */
    protected fun getElemValue(key: List<String>): Map<String, Any> {
        val specificOption = key.first()
        @Suppress("UNCHECKED_CAST")
        return mem.getOrPut(specificOption) {
            return key.asReversed().map { values[it] }
                .fold(HashMap<String, Any>()) { acc, v ->
                    if (v != null) {
                        acc.putAll(v as Map<String, Any>)
                    }
                    acc
                }
        } as Map<String, Any>
    }

    protected fun isElemBlank(key: List<String>): Boolean {
        val blankValue = getElemValue(key)[Elem.BLANK]
        return blankValue != null && blankValue as Boolean
    }

    protected fun getNumber(elem: Map<String, Any>, key: String): Double {
        return (elem.getValue(key) as Number).toDouble()
    }

    protected fun getColor(elem: Map<String, Any>, key: String): Color {
        return when (val value = elem.getValue(key)) {
            is Color -> value
            else -> Colors.parseColor(value as String)
        }
    }
}