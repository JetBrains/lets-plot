/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.scale.ScaleProvider

class TypedScaleProviderMap constructor(map: Map<Aes<*>, ScaleProvider<*>>) {
    private var myMap: Map<Aes<*>, ScaleProvider<*>> = map.toMap()

    operator fun <T> get(aes: Aes<T>): ScaleProvider<T> {
        @Suppress("UNCHECKED_CAST")
        return myMap[aes] as ScaleProvider<T>
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }
}
