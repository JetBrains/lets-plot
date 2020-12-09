/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale

class TypedScaleMap constructor(map: Map<Aes<*>, Scale<*>>) {
    private var myMap: Map<Aes<*>, Scale<*>> = map.toMap()

    operator fun <T> get(aes: Aes<T>): Scale<T> {
        @Suppress("UNCHECKED_CAST")
        return (myMap[aes] as? Scale<T>) ?: error("No scale found for aes $aes")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> safeGet(aes: Aes<T>): Scale<T>? = myMap[aes] as Scale<T>?

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }
}
