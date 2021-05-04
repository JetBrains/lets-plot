/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale

class TypedScaleMap constructor(map: Map<Aes<*>, Scale<*>>) {
    private var myMap: Map<Aes<*>, Scale<*>> = map.toMap()

    operator fun <T> get(aes: Aes<T>): Scale<T> {
        @Suppress("UNCHECKED_CAST")
        return (myMap[aes] as? Scale<T>) ?: run {
            val message = "No scale found for aes: $aes"
            LOG.error(IllegalStateException(message)) { message }
            error(message)
        }
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }

    companion object {
        private val LOG = PortableLogging.logger(TypedScaleMap::class)
    }
}
