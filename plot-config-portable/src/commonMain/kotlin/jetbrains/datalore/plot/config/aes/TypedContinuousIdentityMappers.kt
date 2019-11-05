/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.scale.Mappers
import kotlin.math.abs
import kotlin.math.roundToInt

object TypedContinuousIdentityMappers {
    val COLOR = { n: Double? ->
        if (n == null) {
            null
        } else {
            val value = abs(n.roundToInt())
            Color(
                    value shr 16 and 0xff,
                    value shr 8 and 0xff,
                    value and 0xff
            )
        }
    }

    private val MAP = HashMap<Aes<*>, (Double?) -> Any?>()

    init {
        for (aes in Aes.numeric(Aes.values())) {
            MAP[aes] = Mappers.IDENTITY
        }

        MAP[Aes.COLOR] =
            COLOR
        MAP[Aes.FILL] =
            COLOR
    }

    fun contain(aes: Aes<*>): Boolean {
        return MAP.containsKey(aes)
    }

    operator fun <T> get(aes: Aes<T>): (Double?) -> T? {
        checkArgument(contain(aes), "No continuous identity mapper found for aes " + aes.name)
        val mapper = MAP[aes]!!
        // Safe cast because MAP was initiated in type-safe manner
        @Suppress("UNCHECKED_CAST")
        return mapper as ((Double?) -> T?)
    }
}
