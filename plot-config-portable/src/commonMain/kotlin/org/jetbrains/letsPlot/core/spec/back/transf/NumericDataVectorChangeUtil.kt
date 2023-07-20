/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf

internal object NumericDataVectorChangeUtil {
    fun containsNumbersToConvert(dat: List<*>): Boolean {
        return dat.filterNotNull().any {
            it is Number && it !is Double
        }
    }

    fun convertNumbersToDouble(dat: List<*>): List<*> {
        return dat.map { o: Any? ->
            if (o is Number) o.toDouble() else o
        }
    }
}
