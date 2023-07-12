/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.core.plot.base.Aes

internal class TypedIndexFunctionMap(indexFunctionMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, (Int) -> Any?>) {
    private var myMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, (Int) -> Any?> = indexFunctionMap

    operator fun <T> get(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): (Int) -> T {
        // Safe cast if 'put' is used responsibly.
        @Suppress("UNCHECKED_CAST")
        return myMap[aes] as ((Int) -> T)
    }
}
