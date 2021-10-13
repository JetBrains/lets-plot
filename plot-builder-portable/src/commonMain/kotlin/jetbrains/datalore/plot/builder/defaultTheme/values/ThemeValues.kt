/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

abstract class ThemeValues(
    val values: Map<String, Any>
) {
    operator fun plus(other: Map<String, Any>): Map<String, Any> {
        val result = HashMap<String, Any>(this.values)
        for ((k, v) in other) {
            val wasVal = result.put(k, v)
            if (wasVal is Map<*, *>) {
                result.put(k, wasVal + (v as Map<*, *>))
            }
        }
        return result
    }
}