/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform

import org.jetbrains.letsPlot.core.spec.transform.SpecChange
import org.jetbrains.letsPlot.core.spec.transform.SpecChangeContext
import org.jetbrains.letsPlot.core.spec.back.transform.NumericDataVectorChangeUtil.containsNumbersToConvert
import org.jetbrains.letsPlot.core.spec.back.transform.NumericDataVectorChangeUtil.convertNumbersToDouble

internal class NumericDataVectorSpecChange : SpecChange {
    private fun needChange(l: List<*>): Boolean {
//        for (o in l) {
//            if (o != null) {
//                if (o is Number) {
//                    if (o !is Double) {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
        return containsNumbersToConvert(l)
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val keys = HashSet(spec.keys)
        for (key in keys) {
            val dat = spec.getValue(key)
            require(dat is List<*>) { "The value of data variable [$key] must be a list but was ${dat::class.simpleName}" }
            if (needChange(dat)) {
//                spec[key] = dat.map { o: Any? ->
//                    if (o is Number) o.toDouble() else o
//                }
                spec[key] = convertNumbersToDouble(dat)
            }
        }
    }
}
