/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform

interface SpecChange {
    fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext)
    fun isApplicable(spec: Map<String, Any>): Boolean {
        return true
    }
}
