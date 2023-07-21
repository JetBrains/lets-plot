/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform

interface SpecChangeContext {
    fun getSpecsAbsolute(vararg keys: String): List<Map<String, Any>>
}
