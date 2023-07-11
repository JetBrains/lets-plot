/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.config.Option.Scale.BREAKS
import jetbrains.datalore.plot.config.Option.Scale.CONTINUOUS_TRANSFORM
import jetbrains.datalore.plot.config.Option.Scale.DATE_TIME
import jetbrains.datalore.plot.config.Option.Scale.DISCRETE_DOMAIN
import jetbrains.datalore.plot.config.Option.Scale.FORMAT
import jetbrains.datalore.plot.config.Option.Scale.LABELS
import jetbrains.datalore.plot.config.Option.Scale.LIMITS
import jetbrains.datalore.plot.config.TestUtil.buildGeomLayer
import org.jetbrains.letsPlot.commons.intern.datetime.*
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleConfigLabelsTest {

    private val data = mapOf("value" to listOf(0, 1))
    private val mappingXY = mapOf(Aes.X.name to "value", Aes.Y.name to "value")
    private val discreteData = mapOf("value" to listOf('a', 'b', 'c'))

    @Test
    fun `default scale`() {
        val scaleMap = getScaleMap(data, mappingXY, scales = emptyList())
        val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
        val yLabels = getScaleLabels(scaleMap.getValue(Aes.Y))

        assertEquals(listOf("-0.4", "-0.2", "0.0", "0.2", "0.4"), xLabels)
        assertEquals(listOf("-0.4", "-0.2", "0.0", "0.2", "0.4"), yLabels)
    }

    @Test
    fun `set format for labels of the continuous scale`() {
        val scaleMap = getScaleMap(
            data,
            mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    FORMAT to ".2f"
                ),
                mapOf(
                    Option.Scale.AES to Aes.Y.name,
                    FORMAT to ".3f"
                )
            )
        )
        val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
        val yLabels = getScaleLabels(scaleMap.getValue(Aes.Y))

        assertEquals(listOf("-0.40", "-0.20", "0.00", "0.20", "0.40"), xLabels)
        assertEquals(listOf("-0.400", "-0.200", "0.000", "0.200", "0.400"), yLabels)
    }

    @Test
    fun `set format for labels of the log scale`() {
        run {
            //default
            val scaleMap = getScaleMap(
                data,
                mappingXY,
                scales = listOf(
                    mapOf(
                        Option.Scale.AES to Aes.X.name,
                        CONTINUOUS_TRANSFORM to "log10"
                    ),
                    mapOf(
                        Option.Scale.AES to Aes.Y.name,
                        CONTINUOUS_TRANSFORM to "log10"
                    )
                )
            )
            val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
            val yLabels = getScaleLabels(scaleMap.getValue(Aes.Y))

            assertEquals(listOf("0.4", "0.6", "1.0", "1.6", "2.5"), xLabels)
            assertEquals(listOf("0.4", "0.6", "1.0", "1.6", "2.5"), yLabels)
        }
        run {
            val scaleMap = getScaleMap(
                data,
                mappingXY,
                scales = listOf(
                    mapOf(
                        Option.Scale.AES to Aes.X.name,
                        CONTINUOUS_TRANSFORM to "log10",
                        FORMAT to "x = {}"
                    ),
                    mapOf(
                        Option.Scale.AES to Aes.Y.name,
                        CONTINUOUS_TRANSFORM to "log10",
                        FORMAT to "y = {.2f}"
                    )
                )
            )
            val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
            val yLabels = getScaleLabels(scaleMap.getValue(Aes.Y))

            assertEquals(
                listOf(
                    "x = 0.3981071705534972",
                    "x = 0.6309573444801932",
                    "x = 1.0",
                    "x = 1.5848931924611136",
                    "x = 2.51188643150958"
                ), xLabels
            )
            // todo {} should use the default formatted value:
            //      assertEquals(listOf("x = 0.4", "x = 0.6", "x = 1.0", "x = 1.0", "x = 2.5"), xLabels)

            assertEquals(listOf("y = 0.40", "y = 0.63", "y = 1.00", "y = 1.58", "y = 2.51"), yLabels)
        }
        run {
            val scaleMap = getScaleMap(
                data,
                mappingXY,
                scales = listOf(
                    mapOf(
                        Option.Scale.AES to Aes.X.name,
                        CONTINUOUS_TRANSFORM to "log10",
                        FORMAT to ".2f"
                    ),
                    mapOf(
                        Option.Scale.AES to Aes.Y.name,
                        CONTINUOUS_TRANSFORM to "log10",
                        FORMAT to ".3f"
                    )
                )
            )
            val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
            val yLabels = getScaleLabels(scaleMap.getValue(Aes.Y))

            assertEquals(listOf("0.40", "0.63", "1.00", "1.58", "2.51"), xLabels)
            assertEquals(listOf("0.398", "0.631", "1.000", "1.585", "2.512"), yLabels)
        }
    }

    @Test
    fun `skip format parameter if breaks and labels are specified`() {
        val scaleMap = getScaleMap(
            data,
            mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    BREAKS to listOf(-0.5, 0.5, 1.5),
                    LABELS to listOf("-0.5", "0.5", "1.5"),
                    FORMAT to ".2f"
                )
            )
        )
        val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
        assertEquals(listOf("-0.5", "0.5", "1.5"), xLabels)
    }

    @Test
    fun `set format for labels of the discrete scale`() {
        val scaleMap = getScaleMap(
            discreteData,
            mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    DISCRETE_DOMAIN to true,
                    FORMAT to "x = is {}"
                ),
                mapOf(
                    Option.Scale.AES to Aes.Y.name,
                    DISCRETE_DOMAIN to true
                )
            )
        )
        val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
        val yLabels = getScaleLabels(scaleMap.getValue(Aes.Y))

        assertEquals(listOf("x = is a", "x = is b", "x = is c"), xLabels)
        assertEquals(listOf("a", "b", "c"), yLabels)
    }

    @Test
    fun `format discrete scale with limits parameter`() {
        val scaleMap = getScaleMap(
            discreteData,
            mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    LIMITS to listOf('a', 'b'),
                    FORMAT to "is {}"
                )
            )
        )
        val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
        assertEquals(listOf("is a", "is b"), xLabels)
    }

    @Test
    fun `discrete scale parameter reverse should work with limits`() {
        val scaleMap = getScaleMap(
            discreteData,
            mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    LIMITS to listOf('a', 'b'),
                    Option.Scale.DISCRETE_DOMAIN_REVERSE to true,
                    FORMAT to "is {}"
                )
            )
        )
        val xLabels = getScaleLabels(scaleMap.getValue(Aes.X))
        assertEquals(listOf("is b", "is a"), xLabels)
    }

    @Test
    fun datetime() {
        val instant = TimeZone.UTC.toInstant(
            DateTime(
                Date(1, Month.JANUARY, 2021),
                Time(10, 10)
            )
        ).timeSinceEpoch.toDouble()

        val scaleMap = getScaleMap(
            data = mapOf(
                "value" to listOf(instant)
            ),
            mapping = mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    DATE_TIME to true,
                    FORMAT to "%m-%d-%Y %H:%M"
                ),
                mapOf(
                    Option.Scale.AES to Aes.Y.name,
                    DATE_TIME to true,
                    FORMAT to "%B %Y"
                )
            )
        )

        val xLabels = getScaleLabels(
            scaleMap.getValue(Aes.X),
            targetCount = 1,
            closeRange = DoubleSpan(instant, instant)
        )
        assertEquals(listOf("01-01-2021 10:10"), xLabels)

        val yLabels = getScaleLabels(
            scaleMap.getValue(Aes.Y),
            targetCount = 1,
            closeRange = DoubleSpan(instant, instant)
        )
        assertEquals(listOf("January 2021"), yLabels)
    }

    @Test
    fun `DateTime format should be applied to the breaks`() {
        val instants = List(3) {
            DateTime(Date(1, Month.JANUARY, 2021)).add(Duration.DAY.mul(it.toLong()))
        }.map { TimeZone.UTC.toInstant(it).timeSinceEpoch.toDouble() }

        val scaleMap = getScaleMap(
            data = mapOf(
                "value" to listOf(instants.first())
            ),
            mapping = mappingXY,
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    DATE_TIME to true,
                    FORMAT to "%d-%m-%Y",
                    BREAKS to instants
                )
            )
        )

        val xLabels = getScaleLabels(
            scaleMap.getValue(Aes.X),
            targetCount = 1,
            closeRange = DoubleSpan(instants.first(), instants.last())
        )
        assertEquals(
            expected = listOf("01-01-2021", "02-01-2021", "03-01-2021"),
            xLabels
        )
    }

    @Test
    fun `set format for the non positional scale`() {
        val data = mapOf("value" to listOf(1, 2, 3), "c" to listOf("red", "green", "blue"))
        val mapping = mapOf(
            Aes.X.name to "value",
            Aes.Y.name to "value",
            Aes.COLOR.name to "c",
        )
        val scales = listOf(
            mapOf(
                Option.Scale.AES to Aes.COLOR.name,
                Option.Scale.SCALE_MAPPER_KIND to "identity",
                FORMAT to "is {}"
            )
        )
        val scaleMap = getScaleMap(data, mapping, scales)

        val labels = scaleMap.getValue(Aes.COLOR).getScaleBreaks().labels
        assertEquals(listOf("is red", "is green", "is blue"), labels)
    }

    @Test
    fun `aes_label with discrete input`() {
        val serie = listOf("one", "two", "three")
        val data = mapOf("value" to serie)
        val mapping = mapOf(
            Aes.LABEL.name to "value",
        )
        val scaleMap = getScaleMap(data, mapping, emptyList(), Option.GeomName.TEXT)

        val labels = scaleMap.getValue(Aes.LABEL).getScaleBreaks().labels
        // identity expected
        assertEquals(serie, labels)
    }

    @Test
    fun `aes_label with continuous input`() {
        val serie = listOf(1.0, 2.0, 3.0)
        val data = mapOf("value" to serie)
        val mapping = mapOf(
            Aes.LABEL.name to "value",
        )

        val geomLayer = buildGeomLayer(Option.GeomName.TEXT, data, mapping, null, emptyList())

        val labelTransform = geomLayer.scaleMap.getValue(Aes.LABEL).transform
        val labelMapper = geomLayer.scaleMappersNP.getValue(Aes.LABEL)

        val inputs = listOf(null, 1.5, -1.5)
        val outputs = labelTransform.apply(inputs).map {
            labelMapper(it) as Double?
        }

        // continuous identity expected
        assertEquals(inputs, outputs)
    }

    @Test
    fun `aes_label with continuous input and format`() {
        val serie = listOf(1.0, 2.0, 3.0)
        val data = mapOf("value" to serie)
        val mapping = mapOf(
            Aes.LABEL.name to "value",
        )
        val scales = listOf(
            mapOf(
                Option.Scale.AES to Aes.LABEL.name,
                FORMAT to "d"   // round to int.
            )
        )

        val geomLayer = buildGeomLayer(Option.GeomName.TEXT, data, mapping, null, scales)

        val labelTransform = geomLayer.scaleMap.getValue(Aes.LABEL).transform
        val labelMapper = geomLayer.scaleMappersNP.getValue(Aes.LABEL)

        val inputs = listOf(null, 1.5, -1.5)
        val outputs = labelTransform.apply(inputs).map {
            (labelMapper(it) as Double?)?.roundToInt()?.toString() ?: "n/a"
        }

        // continuous identity expected (formatted to string).
        assertEquals(listOf("n/a", "2", "-1"), outputs)
    }

    companion object {
        private fun getScaleMap(
            data: Map<String, Any>,
            mapping: Map<String, Any>,
            scales: List<Map<String, Any>>,
            geom: String = Option.GeomName.POINT,
        ) = buildGeomLayer(geom, data, mapping, scales = scales).scaleMap

        internal fun getScaleLabels(
            scale: Scale,
            targetCount: Int = 5,
            closeRange: DoubleSpan = DoubleSpan(-0.5, 0.5),
        ): List<String> {
            val breaksProvider = AxisBreaksProviderFactory.forScale(scale).createAxisBreaksProvider(closeRange)
            return breaksProvider.getBreaks(
                targetCount,
                axisLength = 0.0  // actually the axisLength parameter is not used to get breaks
            ).labels
        }
    }
}