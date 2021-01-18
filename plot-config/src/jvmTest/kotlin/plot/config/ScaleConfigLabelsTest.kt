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
import jetbrains.datalore.plot.builder.GeomLayer
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
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleConfigLabelsTest {

    private val myData = mapOf(
        "x" to listOf(0, 1),
        "y" to listOf(0, 1)
    )
    private val myCloseRange = ClosedRange(-0.5, 0.5)

    @Test
    fun `default scale`() {
        val geomLayer = geomLayer(scales = emptyList())
        val xLabels = getScaleLabels(geomLayer.scaleMap[Aes.X])
        val yLabels = getScaleLabels(geomLayer.scaleMap[Aes.Y])


        assertEquals(listOf("-0.4", "-0.2", "0.0", "0.2", "0.4"), xLabels)
        assertEquals(listOf("-0.4", "-0.2", "0.0", "0.2", "0.4"), yLabels)
    }

    @Test
    fun `set format for labels of the continuous scale`() {
        val geomLayer = geomLayer(
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
        val xLabels = getScaleLabels(geomLayer.scaleMap[Aes.X])
        val yLabels = getScaleLabels(geomLayer.scaleMap[Aes.Y])

        assertEquals(listOf("-0.40", "-0.20", "0.00", "0.20", "0.40"), xLabels)
        assertEquals(listOf("-0.400", "-0.200", "0.000", "0.200", "0.400"), yLabels)
    }

    @Test
    fun `set format for labels of the log scale`() {
        val geomLayer = geomLayer(
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
        val xLabels = getScaleLabels(geomLayer.scaleMap[Aes.X])
        val yLabels = getScaleLabels(geomLayer.scaleMap[Aes.Y])

        assertEquals(listOf("0.40", "0.63", "1.00", "1.58", "2.51"), xLabels)
        assertEquals(listOf("0.398", "0.631", "1.000", "1.585", "2.512"), yLabels)

    }

    @Test
    fun `set format for labels of the discrete scale`() {
        val geomLayer = geomLayer(
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    DISCRETE_DOMAIN to true,
                    FORMAT to "x = {d}"
                ),
                mapOf(
                    Option.Scale.AES to Aes.Y.name,
                    DISCRETE_DOMAIN to true,
                    FORMAT to "y = {.1f}"
                )
            )
        )
        val xLabels = getScaleLabels(geomLayer.scaleMap[Aes.X])
        val yLabels = getScaleLabels(geomLayer.scaleMap[Aes.Y])

        assertEquals(listOf("x = 0", "x = 1"), xLabels)
        assertEquals(listOf("y = 0.0", "y = 1.0"), yLabels)
    }

    @Test
    fun `skip format parameter if breaks and labels are specified`() {
        val geomLayer = geomLayer(
            scales = listOf(
                mapOf(
                    Option.Scale.AES to Aes.X.name,
                    BREAKS to listOf(-0.5, 0.5, 1.5),
                    LABELS to listOf("-0.5", "0.5", "1.5"),
                    FORMAT to ".2f"
                )
            )
        )
        val xLabels = getScaleLabels(geomLayer.scaleMap[Aes.X])
        assertEquals(listOf("-0.5", "0.5", "1.5"), xLabels)
    }

    @Test
    fun datetime() {
        val instant = TimeZone.UTC.toInstant(
            DateTime(
                Date(1, Month.JANUARY, 2021),
                Time(10, 10)
            )
        ).timeSinceEpoch.toDouble()

        val geomLayer = geomLayer(
            data = mapOf(
                "x" to listOf(instant),
                "y" to listOf(instant)
            ),
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
            geomLayer.scaleMap[Aes.X],
            targetCount = 1,
            closeRange = ClosedRange(instant, instant)
        )
        assertEquals(listOf("01-01-2021 10:10"), xLabels)

        val yLabels = getScaleLabels(
            geomLayer.scaleMap[Aes.Y],
            targetCount = 1,
            closeRange = ClosedRange(instant, instant)
        )
        assertEquals(listOf("January 2021"), yLabels)
    }


    private fun geomLayer(data: Map<String, Any> = myData, scales: List<Map<String, Any>>): GeomLayer {
        val plotOpts = mutableMapOf(
            Option.Meta.KIND to Option.Meta.Kind.PLOT,
            DATA to data,
            MAPPING to mapOf(Aes.X.name to "x", Aes.Y.name to "y"),
            LAYERS to listOf(
                mapOf(
                    GEOM to POINT
                )
            ),
            SCALES to scales
        )
        val transformed = ServerSideTestUtil.serverTransformWithoutEncoding(plotOpts)
        return PlotConfigClientSideUtil.createPlotAssembler(transformed).layersByTile.single().single()
    }

    private fun getScaleLabels(
        scale: Scale<Double>,
        targetCount: Int = 6,
        closeRange: ClosedRange<Double> = myCloseRange
    ): List<String> {
        val breaksProvider = AxisBreaksUtil.createAxisBreaksProvider(scale, closeRange)
        return breaksProvider.getBreaks(
            targetCount,
            axisLength = 0.0  // actually the axisLength parameter is not used to get breaks
        ).labels
    }
}