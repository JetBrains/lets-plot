/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme.LegendTheme

internal object LegendAssemblerUtil {
    fun <T> mapToAesthetics(
        valuesByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<T>, List<T>>, constantByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<T>, T>, aestheticsDefaults: AestheticsDefaults
    ): Aesthetics {
        val builder = AestheticsBuilder(0)
        for (aes in org.jetbrains.letsPlot.core.plot.base.Aes.values()) {
            @Suppress("UNCHECKED_CAST")
            builder.constantAes(aes as org.jetbrains.letsPlot.core.plot.base.Aes<Any>, aestheticsDefaults.defaultValue(aes))
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
        valueByAesIterable: Collection<Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>>,
        constantByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
        colorByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>,
        fillByAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>
    ): Aesthetics {
        val dataPoints = ArrayList<Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>>()
        for (valueByAes in valueByAesIterable) {
            val dataPoint = HashMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>()
            for (aes in org.jetbrains.letsPlot.core.plot.base.Aes.values()) {
                dataPoint[aes] = aestheticsDefaults.defaultValueInLegend(aes)!!
            }

            // Derive some aesthetics from constants
            for (constantAes in constantByAes.keys) {
                when (constantAes) {
                    org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE,
                    org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                    org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                    org.jetbrains.letsPlot.core.plot.base.Aes.PAINT_A, org.jetbrains.letsPlot.core.plot.base.Aes.PAINT_B, org.jetbrains.letsPlot.core.plot.base.Aes.PAINT_C -> dataPoint[constantAes] = constantByAes[constantAes]!!
                }
            }

            for (aes in valueByAes.keys) {
                dataPoint[aes] = valueByAes[aes]!!
            }

            dataPoints.add(dataPoint)
        }

        val builder = AestheticsBuilder(dataPoints.size)
        for (aes in org.jetbrains.letsPlot.core.plot.base.Aes.values()) {
            @Suppress("UNCHECKED_CAST")
            builder.aes(aes as org.jetbrains.letsPlot.core.plot.base.Aes<Any>) { index -> dataPoints[index][aes]!! }
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
