/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

abstract class ThemeValues(
    val values: Map<String, Any>
) {
    operator fun plus(other: Map<String, Any>): Map<String, Any> {
        return values.mergeWith(other)
    }

    companion object {
        fun Map<String, Any>.mergeWith(other: Map<String, Any>): Map<String, Any> {
            val result = HashMap<String, Any>(this)
            for ((k, v) in other) {
                val wasVal = result.put(k, v)
                if (wasVal is Map<*, *>) {
                    result.put(k, wasVal + (v as Map<*, *>))
                }
            }
            return result
        }

        fun forName(theme: String): ThemeValues {
            return when (theme) {
                ThemeOption.Name.R_GREY -> ThemeValuesRGrey()
                ThemeOption.Name.R_LIGHT -> ThemeValuesRLight()
                ThemeOption.Name.R_CLASSIC -> ThemeValuesRClassic()
                ThemeOption.Name.R_MINIMAL -> ThemeValuesRMinimal()
                ThemeOption.Name.R_BW -> ThemeValuesRBW()
                ThemeOption.Name.LP_MINIMAL -> ThemeValuesLPMinimal2()
                ThemeOption.Name.LP_NONE -> ThemeValuesLPNone()
                else -> throw IllegalArgumentException("Unsupported theme: '$theme'")
            }
        }
    }
}