/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.spec.Option
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ScaleFormatWhenDiscreteDateTimeTest {

    @Test
    fun `both - continuous and discrete scale labels - should be formatted as date-time`() {
        val instants = List(5) {
            DateTime(Date(1, Month.JANUARY, 2021)).add(Duration.DAY.mul(it.toLong()))
        }.map { TimeZone.UTC.toInstant(it).timeSinceEpoch.toDouble() }

        // For a discrete scale, a formatter is applied as for a continuous scale
        val expectedLabels = listOf(
            "Jan 1", "Jan 2", "Jan 3", "Jan 4", "Jan 5"
        )

        checkScales(instants, expectedLabels, expectedLabels)
    }

    @Test
    fun `data when discrete scale chooses a better formatter than the continuous scale`() {
        val instants = List(3) {
            DateTime(Date(1, Month.JANUARY, 2021)).add(Duration.DAY.mul(it.toLong()))
        }.map { TimeZone.UTC.toInstant(it).timeSinceEpoch.toDouble() }

        val formattedForContinuous = listOf(
            "00:00", "12:00", "00:00", "12:00", "00:00"
        )
        // For discrete scale: if to get the DateTimeBreaksHelper's formatter (which the continuous scale uses),
        // the labels will be formatted as follows: [00:00, 00:00, 00:00]
        // => better formatter will be applied
        val formattedForDiscrete = listOf(
            "2021-01-01", "2021-01-02", "2021-01-03"
        )

        checkScales(instants, formattedForDiscrete, formattedForContinuous)
    }

    private fun checkScaleLabels(
        dataValues: List<Double>,
        discreteScales: List<Aes<*>>,
        asDiscreteAes: List<Aes<*>>,
        expectedLabelsForDiscrete: List<String>,
        expectedLabelForContinuous: List<String>
    ) {
        val geomLayer = TestingGeomLayersBuilder.getSingleGeomLayer(
            plotSpec(dataValues, discreteScales, asDiscreteAes)
        )

        fun checkFormatting(aes: Aes<*>, isDiscreteScale: Boolean) {
            assertTrue(aes in geomLayer.scaleMap)
            val scale = geomLayer.scaleMap[aes]!!

            assertTrue(scale.isContinuous != isDiscreteScale)
            if (scale.isContinuous) {
                val breaksGenerator =
                    (scale.getBreaksGenerator() as Transforms.BreaksGeneratorForTransformedDomain).breaksGenerator
                val range = DoubleSpan.encloseAllQ(dataValues)
                assertNotNull(range)
                val scaleLabels = breaksGenerator.generateBreaks(range, dataValues.size).labels
                assertEquals(expectedLabelForContinuous, scaleLabels, "Wrong scale labels for $aes")
            } else {
                assertEquals(expectedLabelsForDiscrete, scale.getScaleBreaks().labels, "Wrong scale labels for $aes")
            }
        }

        fun isDiscreteScale(aes: Aes<*>) = aes in discreteScales || aes in asDiscreteAes

        checkFormatting(Aes.X, isDiscreteScale(Aes.X))
        checkFormatting(Aes.COLOR, isDiscreteScale(Aes.COLOR))
    }

    private fun checkScales(
        dataValues: List<Double>,
        expectedLabelsForDiscrete: List<String>,
        expectedLabelForContinuous: List<String>
    ) {
        checkScaleLabels(
            dataValues,
            discreteScales = emptyList(),
            asDiscreteAes = emptyList(),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous
        )
        checkScaleLabels(
            dataValues,
            discreteScales = emptyList(),
            asDiscreteAes = listOf(Aes.COLOR),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous
        )
        checkScaleLabels(
            dataValues,
            discreteScales = emptyList(),
            asDiscreteAes = listOf(Aes.X, Aes.COLOR),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous
        )

        checkScaleLabels(
            dataValues,
            discreteScales = listOf(Aes.COLOR),
            asDiscreteAes = emptyList(),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous
        )

        checkScaleLabels(
            dataValues,
            discreteScales = listOf(Aes.X, Aes.COLOR),
            asDiscreteAes = emptyList(),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous
        )
    }

    private fun plotSpec(
        instants: List<Double>,
        discreteScales: List<Aes<*>>,
        asDiscreteAes: List<Aes<*>>,
    ): MutableMap<String, Any> {
        fun discreteScale(aes: Aes<*>) = mapOf(
            Option.Scale.AES to aes.name,
            Option.Scale.DISCRETE_DOMAIN to true
        )

        fun asDiscreteAnnotation(aes: Aes<*>) = mapOf(
            Option.Meta.MappingAnnotation.AES to aes.name,
            Option.Meta.MappingAnnotation.ANNOTATION to Option.Meta.MappingAnnotation.AS_DISCRETE
        )

        fun mappingAnnotation(aesList: List<Aes<*>>) = mapOf(
            Option.Meta.MappingAnnotation.TAG to aesList.map(::asDiscreteAnnotation)
        )

        fun dateTimeAnnotation(columnName: String) = mapOf(
            Option.Meta.SeriesAnnotation.TAG to listOf(
                mapOf(
                    Option.Meta.SeriesAnnotation.COLUMN to columnName,
                    Option.Meta.SeriesAnnotation.TYPE to Option.Meta.SeriesAnnotation.Types.DATE_TIME
                )
            )
        )

        return mutableMapOf(
            Option.Meta.KIND to Option.Meta.Kind.PLOT,
            Option.PlotBase.DATA to mapOf("v" to instants),
            Option.PlotBase.MAPPING to mapOf(
                Aes.X.name to "v",
                Aes.COLOR.name to "v",
            ),
            Option.Plot.LAYERS to listOf(
                mapOf(Option.Layer.GEOM to Option.GeomName.POINT)
            ),
            Option.Plot.SCALES to discreteScales.map(::discreteScale),
            Option.Meta.DATA_META to dateTimeAnnotation("v") + mappingAnnotation(asDiscreteAes)
        )
    }
}