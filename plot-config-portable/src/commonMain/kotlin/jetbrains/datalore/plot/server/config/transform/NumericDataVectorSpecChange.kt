/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext

internal class NumericDataVectorSpecChange : SpecChange {
    private fun needChange(l: List<*>): Boolean {
        for (o in l) {
            if (o != null) {
                if (o is Number) {
                    if (o !is Double) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val keys = HashSet(spec.keys)
        for (key in keys) {
            val dat = spec[key]!!
            require(dat is List<*>) { "The value of data variable [$key] must be a list but was ${dat::class.simpleName}" }
            if (needChange(dat)) {
                spec[key] = dat.map { o: Any? ->
                    if (o is Number) o.toDouble() else o
                }
            }
        }
    }
}
