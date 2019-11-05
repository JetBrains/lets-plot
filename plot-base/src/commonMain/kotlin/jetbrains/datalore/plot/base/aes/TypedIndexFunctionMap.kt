/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.plot.base.Aes

internal class TypedIndexFunctionMap(indexFunctionMap: Map<Aes<*>, (Int) -> Any?>) {
    private var myMap: Map<Aes<*>, (Int) -> Any?> = indexFunctionMap

    operator fun <T> get(aes: Aes<T>): (Int) -> T {
        // Safe cast if 'put' is used responsibly.
        @Suppress("UNCHECKED_CAST")
        return myMap[aes] as ((Int) -> T)
    }
}
