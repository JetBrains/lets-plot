/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.conversion

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
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

    val SHAPE = object : ScaleMapper<PointShape> {
        override fun invoke(v: Double?): PointShape? {
            return v?.let {
                NamedShape.fromInt(it.roundToInt())
            }
        }
    }

    val LINETYPE = object : ScaleMapper<LineType> {
        override fun invoke(v: Double?): LineType? {
            return v?.let {
                NamedLineType.fromInt(it.roundToInt())
            }
        }
    }

    private val MAP = HashMap<Aes<*>, ScaleMapper<*>>()

    init {
        for (aes in Aes.numeric(Aes.values())) {
            MAP[aes] = Mappers.IDENTITY
        }
        MAP[Aes.COLOR] = COLOR
        MAP[Aes.FILL] = COLOR
        MAP[Aes.SHAPE] = SHAPE
        MAP[Aes.LINETYPE] = LINETYPE
    }

    fun contain(aes: Aes<*>): Boolean {
        return MAP.containsKey(aes)
    }

    operator fun <T> get(aes: Aes<T>): ScaleMapper<T> {
        require(contain(aes)) { "No continuous identity mapper found for aes " + aes.name }
        val mapper = MAP.getValue(aes)
        // Safe cast because MAP was initiated in a type-safe manner
        @Suppress("UNCHECKED_CAST")
        return mapper as ScaleMapper<T>
    }
}
