/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale

// TopDo: Remove as the scale is no longer need to have generic type.
class TypedScaleMap constructor(val map: Map<Aes<*>, Scale>) {
    operator fun <T> get(aes: Aes<T>): Scale {
        @Suppress("UNCHECKED_CAST")
        return (map[aes] as? Scale) ?: run {
            val message = "No scale found for aes: $aes"
            LOG.error(IllegalStateException(message)) { message }
            error(message)
        }
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return map.containsKey(aes)
    }

    fun keySet(): Set<Aes<*>> {
        return map.keys
    }

    companion object {
        private val LOG = PortableLogging.logger(TypedScaleMap::class)
    }
}
