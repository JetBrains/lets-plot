/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import kotlin.math.abs
import kotlin.math.roundToInt

object TypedContinuousIdentityMappers {
    val COLOR = object : ScaleMapper<Color> {
        override fun invoke(v: Double?): Color? {
            return if (v == null) {
                null
            } else {
                val value = abs(v.roundToInt())
                Color(
                    value shr 16 and 0xff,
                    value shr 8 and 0xff,
                    value and 0xff
                )
            }
        }
    }

    private val MAP = HashMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>()

    init {
        for (aes in org.jetbrains.letsPlot.core.plot.base.Aes.numeric(org.jetbrains.letsPlot.core.plot.base.Aes.values())) {
            MAP[aes] = Mappers.IDENTITY
        }
        MAP[org.jetbrains.letsPlot.core.plot.base.Aes.COLOR] = COLOR
        MAP[org.jetbrains.letsPlot.core.plot.base.Aes.FILL] = COLOR
    }

    fun contain(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        return MAP.containsKey(aes)
    }

    operator fun <T> get(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): ScaleMapper<T> {
        require(contain(aes)) { "No continuous identity mapper found for aes " + aes.name }
        val mapper = MAP[aes]!!
        // Safe cast because MAP was initiated in type-safe manner
        @Suppress("UNCHECKED_CAST")
        return mapper as ScaleMapper<T>
    }
}
