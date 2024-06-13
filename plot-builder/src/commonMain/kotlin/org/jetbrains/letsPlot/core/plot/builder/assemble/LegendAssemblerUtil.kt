/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

internal object LegendAssemblerUtil {
    fun <T> mapToAesthetics(
        valuesByAes: Map<Aes<T>, List<T>>,
        constantByAes: Map<Aes<T>, T>,
        aestheticsDefaults: AestheticsDefaults
    ): Aesthetics {
        val builder = AestheticsBuilder(0)
        for (aes in Aes.values()) {
            @Suppress("UNCHECKED_CAST")
            builder.constantAes(
                aes as Aes<Any>,
                aestheticsDefaults.defaultValue(aes)
            )
        }
        for (aes in valuesByAes.keys) {
            val values = valuesByAes.getValue(aes)
            builder.aes(aes, AestheticsBuilder.list(values))
            builder.dataPointCount(values.size)
        }
        for (aes in constantByAes.keys) {
            builder.constantAes<T>(aes, constantByAes[aes]!!)
        }
        return builder.build()
    }


    fun mapToAesthetics(
        valueByAesIterable: Collection<Map<Aes<*>, Any>>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>
    ): Aesthetics {
        val dataPoints = ArrayList<Map<Aes<*>, Any>>()
        for (valueByAes in valueByAesIterable) {
            val dataPoint = HashMap<Aes<*>, Any>()
            for (aes in Aes.values()) {
                dataPoint[aes] = aestheticsDefaults.defaultValueInLegend(aes)!!

                // fix defaults for 'color_by/fill_by' (https://github.com/JetBrains/lets-plot/issues/867)
                if (aes in listOf(Aes.PAINT_A, Aes.PAINT_B, Aes.PAINT_C)) {
                    val baseAes = when (aes) {
                        colorByAes -> Aes.COLOR
                        fillByAes -> Aes.FILL
                        else -> aes
                    }
                    dataPoint[aes] = aestheticsDefaults.defaultValueInLegend(baseAes)!!
                }
            }

            // Derive from constants
            for (constantAes in constantByAes.keys) {
                dataPoint[constantAes] = constantByAes[constantAes]!!
            }

            for (aes in valueByAes.keys) {
                dataPoint[aes] = valueByAes[aes]!!
            }

            dataPoints.add(dataPoint)
        }

        val builder = AestheticsBuilder(dataPoints.size)
        for (aes in Aes.values()) {
            @Suppress("UNCHECKED_CAST")
            builder.aes(aes as Aes<Any>) { index -> dataPoints[index][aes]!! }
        }

        builder
            .colorAes(colorByAes)
            .fillAes(fillByAes)

        return builder.build()
    }

    fun legendDirection(theme: LegendTheme): LegendDirection {
        var legendDirection = theme.direction()
        if (legendDirection === LegendDirection.AUTO) {
            val legendPosition = theme.position()
            legendDirection =
                if (legendPosition === LegendPosition.TOP || legendPosition === LegendPosition.BOTTOM)
                    LegendDirection.HORIZONTAL
                else
                    LegendDirection.VERTICAL
        }
        return legendDirection
    }
}
