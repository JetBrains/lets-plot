/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.spec.Option
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal object ScaleFomateDateTimeTestUtil {
    val TZ_UTC = TimeZone.UTC
    val TZ_UTC_8 = TimeZone("UTC+8")

    fun checkScaleLabels(
        dataValues: List<Double>,
        discreteScales: List<Aes<*>>,
        asDiscreteAes: List<Aes<*>>,
        expectedLabelsForDiscrete: List<String>,
        expectedLabelForContinuous: List<String>,
        datetimeAnnotationPart: Map<String, String>,
    ) {
        val geomLayer = TestingGeomLayersBuilder.getSingleGeomLayer(
            plotSpec(dataValues, discreteScales, asDiscreteAes, datetimeAnnotationPart)
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

    fun checkScales(
        dataValues: List<Double>,
        expectedLabelsForDiscrete: List<String>,
        expectedLabelForContinuous: List<String>,
        datetimeAnnotationPart: Map<String, String>,
    ) {
        checkScaleLabels(
            dataValues,
            discreteScales = emptyList(),
            asDiscreteAes = emptyList(),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous,
            datetimeAnnotationPart
        )
        checkScaleLabels(
            dataValues,
            discreteScales = emptyList(),
            asDiscreteAes = listOf(Aes.COLOR),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous,
            datetimeAnnotationPart
        )
        checkScaleLabels(
            dataValues,
            discreteScales = listOf(Aes.COLOR),
            asDiscreteAes = emptyList(),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous,
            datetimeAnnotationPart
        )
        checkScaleLabels(
            dataValues,
            discreteScales = emptyList(),
            asDiscreteAes = listOf(Aes.X, Aes.COLOR),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous,
            datetimeAnnotationPart
        )

        checkScaleLabels(
            dataValues,
            discreteScales = listOf(Aes.X, Aes.COLOR),
            // Positional aes must be annotated 'as_discrete'
            asDiscreteAes = listOf(Aes.X),
            expectedLabelsForDiscrete,
            expectedLabelForContinuous,
            datetimeAnnotationPart
        )

        // If positional aes is not annotated 'as_discrete'
        // then it is not taken in account by a discrete axis
        checkScaleLabels(
            dataValues,
            discreteScales = listOf(Aes.X),
            asDiscreteAes = emptyList(),
            expectedLabelsForDiscrete = emptyList(),
            expectedLabelForContinuous,
            datetimeAnnotationPart
        )
    }

    private fun plotSpec(
        instants: List<Double>,
        discreteScales: List<Aes<*>>,
        asDiscreteAes: List<Aes<*>>,
        datetimeAnnotationPart: Map<String, String>,
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
                ) + datetimeAnnotationPart
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