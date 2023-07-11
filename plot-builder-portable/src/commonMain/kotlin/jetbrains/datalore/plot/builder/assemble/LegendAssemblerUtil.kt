/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme.LegendTheme

internal object LegendAssemblerUtil {
    fun <T> mapToAesthetics(
        valuesByAes: Map<Aes<T>, List<T>>, constantByAes: Map<Aes<T>, T>, aestheticsDefaults: AestheticsDefaults
    ): Aesthetics {
        val builder = AestheticsBuilder(0)
        for (aes in Aes.values()) {
            @Suppress("UNCHECKED_CAST")
            builder.constantAes(aes as Aes<Any>, aestheticsDefaults.defaultValue(aes))
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
            }

            // Derive some aesthetics from constants
            for (constantAes in constantByAes.keys) {
                when (constantAes) {
                    Aes.SHAPE,
                    Aes.COLOR,
                    Aes.FILL,
                    Aes.PAINT_A, Aes.PAINT_B, Aes.PAINT_C -> dataPoint[constantAes] = constantByAes[constantAes]!!
                }
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
