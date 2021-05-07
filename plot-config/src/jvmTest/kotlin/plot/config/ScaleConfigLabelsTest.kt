/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.Time
import jetbrains.datalore.base.datetime.tz.TimeZone
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksUtil
import jetbrains.datalore.plot.config.Option.GeomName.POINT
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.Option.Scale.BREAKS
import jetbrains.datalore.plot.config.Option.Scale.CONTINUOUS_TRANSFORM
import jetbrains.datalore.plot.config.Option.Scale.DATE_TIME
import jetbrains.datalore.plot.config.Option.Scale.DISCRETE_DOMAIN
import jetbrains.datalore.plot.config.Option.Scale.FORMAT
import jetbrains.datalore.plot.config.Option.Scale.LABELS
import jetbrains.datalore.plot.config.Option.Scale.LIMITS
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleConfigLabelsTest {

    private val data = mapOf("value" to listOf(0, 1))
    private val mappingXY = mapOf(Aes.X.name to "value", Aes.Y.name to "value")
    private val discreteData = mapOf("value" to listOf('a', 'b', 'c'))

    @Test
    fun `default scale`() {
        val scaleMap = getScaleMap(data, mappingXY, scales = emptyList())
        val xLabels = getScaleLabels(scaleMap[Aes.X])
        val yLabels = getScaleLabels(scaleMap[Aes.Y])

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
        val xLabels = getScaleLabels(scaleMap[Aes.X])
        val yLabels = getScaleLabels(scaleMap[Aes.Y])

        assertEquals(listOf("-0.40", "-0.20", "0.00", "0.20", "0.40"), xLabels)
        assertEquals(listOf("-0.400", "-0.200", "0.000", "0.200", "0.400"), yLabels)
    }

    @Test
    fun `set format for labels of the log scale`() {
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
        val xLabels = getScaleLabels(scaleMap[Aes.X])
        val yLabels = getScaleLabels(scaleMap[Aes.Y])

        assertEquals(listOf("0.40", "0.63", "1.00", "1.58", "2.51"), xLabels)
        assertEquals(listOf("0.398", "0.631", "1.000", "1.585", "2.512"), yLabels)
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
        val xLabels = getScaleLabels(scaleMap[Aes.X])
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
        val xLabels = getScaleLabels(scaleMap[Aes.X])
        val yLabels = getScaleLabels(scaleMap[Aes.Y])

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
        val xLabels = getScaleLabels(scaleMap[Aes.X])
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
        val xLabels = getScaleLabels(scaleMap[Aes.X])
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
            scaleMap[Aes.X],
            targetCount = 1,
            closeRange = ClosedRange(instant, instant)
        )
        assertEquals(listOf("01-01-2021 10:10"), xLabels)

        val yLabels = getScaleLabels(
            scaleMap[Aes.Y],
            targetCount = 1,
            closeRange = ClosedRange(instant, instant)
        )
        assertEquals(listOf("January 2021"), yLabels)
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

        val labels = ScaleUtil.labels(scaleMap[Aes.COLOR])
        assertEquals(listOf("is red", "is green", "is blue"), labels)
    }

    companion object {
        private fun getScaleMap(
            data: Map<String, Any>,
            mapping: Map<String, Any>,
            scales: List<Map<String, Any>>,
        ): TypedScaleMap {
            val plotOpts = mutableMapOf(
                Option.Meta.KIND to Option.Meta.Kind.PLOT,
                DATA to data,
                MAPPING to mapping,
                LAYERS to listOf(
                    mapOf(
                        GEOM to POINT
                    )
                ),
                SCALES to scales
            )
            val transformed = ServerSideTestUtil.serverTransformWithoutEncoding(plotOpts)
            return TestUtil.assertClientWontFail(transformed).scaleMap
        }

        private fun getScaleLabels(
            scale: Scale<Double>,
            targetCount: Int = 5,
            closeRange: ClosedRange<Double> = ClosedRange(-0.5, 0.5),
        ): List<String> {
            val breaksProvider = AxisBreaksUtil.createAxisBreaksProvider(scale, closeRange)
            return breaksProvider.getBreaks(
                targetCount,
                axisLength = 0.0  // actually the axisLength parameter is not used to get breaks
            ).labels
        }
    }
}