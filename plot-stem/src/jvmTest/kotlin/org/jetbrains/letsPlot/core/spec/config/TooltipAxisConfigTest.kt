/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import org.jetbrains.letsPlot.core.commons.time.TimeUtil
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.FORMATS
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.Format.FIELD
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.Format.FORMAT
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.LINES
import org.jetbrains.letsPlot.core.spec.Option.Scale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TooltipAxisConfigTest {
    private val myData = mapOf("v" to listOf(0.34447))
    private val myMapping = mapOf(
        org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "v",
        org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "v"
    )

    // ggplot(data) + geom_point(aes('v','v'), tooltips = layer_tooltips().line('^y').format(...)) + scale_y_*(format=...)
    private fun geomLayer(
        data: Map<String, Any?> = myData,
        mapping: Map<String, Any> = myMapping,
        additionalScaleOption: Pair<String, Any>? = null,
        scaleFormat: String?,
        tooltipFormat: String?,
        useVarNameInTooltip: Boolean = false,
        useTooltipFormatForVarName: Boolean = false
    ): GeomLayer {
        val scales = listOfNotNull(
            Scale.AES to org.jetbrains.letsPlot.core.plot.base.Aes.Y.name,
            scaleFormat?.let { format -> Scale.FORMAT to format },
            additionalScaleOption
        ).toMap()
        val tooltipConfig = listOfNotNull(
            LINES to listOf(if (useVarNameInTooltip) "@v" else "^y"),
            tooltipFormat?.let { format ->
                FORMATS to listOf(
                    mapOf(
                        FIELD to if (useTooltipFormatForVarName) "v" else "^y",
                        FORMAT to format
                    )
                )
            }
        ).toMap()
        return TestUtil.buildPointLayer(data, mapping, tooltips = tooltipConfig, scales = listOf(scales))
    }

    @Test
    fun default() {
        val geomLayer = geomLayer(scaleFormat = null, tooltipFormat = null)
        assertGeneralTooltip(geomLayer, "0.34")
        assertYAxisTooltip(geomLayer, "0.34")
        assertEquals("0.3", getYTick(geomLayer))
    }

    @Test
    fun `scale format does not apply to tooltips`() {
        run {
            val geomLayer = geomLayer(
                scaleFormat = "scale = {} %",    // todo should use the default tick value -> 0.3
                tooltipFormat = null
            )
            assertGeneralTooltip(geomLayer, "0.34")
            assertYAxisTooltip(geomLayer, "0.34")
            // todo assertEquals("scale = 0.3 %", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(scaleFormat = "scale = {.3f} %", tooltipFormat = null)
            assertGeneralTooltip(geomLayer, "0.34")
            assertYAxisTooltip(geomLayer, "0.34")
            assertEquals("scale = 0.300 %", getYTick(geomLayer))
        }
    }

    @Test
    fun `scale_y_discrete(format)`() {
        run {
            val geomLayer = geomLayer(
                additionalScaleOption = Scale.DISCRETE_DOMAIN to true,
                scaleFormat = "scale = {} %",
                tooltipFormat = null
            )
            assertGeneralTooltip(geomLayer, "scale = 0.34447 %")
            assertYAxisTooltip(geomLayer, "scale = 0.34447 %")
            assertEquals("scale = 0.34447 %", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(
                additionalScaleOption = Scale.DISCRETE_DOMAIN to true,
                scaleFormat = "scale = {.4f} %",
                tooltipFormat = null
            )
            assertGeneralTooltip(geomLayer, "scale = 0.3445 %")
            assertYAxisTooltip(geomLayer, "scale = 0.3445 %")
            assertEquals("scale = 0.3445 %", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(
                additionalScaleOption = Scale.DISCRETE_DOMAIN to true,
                scaleFormat = ".4f",
                tooltipFormat = null
            )
            assertGeneralTooltip(geomLayer, "0.3445")
            assertYAxisTooltip(geomLayer, "0.3445")
            assertEquals("0.3445", getYTick(geomLayer))
        }
    }

    // Issue https://github.com/JetBrains/lets-plot/issues/484: '{}' in format pattern should use the default formatting
    //  - for such examples, a message is printed if the formatting result does not match the expected

    @Test
    fun `tooltip format for the 'y'`() {
        run {
            val geomLayer = geomLayer(
                scaleFormat = null,
                tooltipFormat = "tooltip = {} %"     // todo should use the scale's default formatter -> 0.34
            )
            assertGeneralTooltip(geomLayer, "tooltip = 0.34447 %", ::println)  // todo "tooltip = 0.34 %"
            assertYAxisTooltip(geomLayer, "tooltip = 0.34447 %", ::println)    // todo "tooltip = 0.34 %"
            assertEquals("0.3", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(scaleFormat = null, tooltipFormat = "tooltip = {.4f} %")
            assertGeneralTooltip(geomLayer, "tooltip = 0.3445 %")
            assertYAxisTooltip(geomLayer, "tooltip = 0.3445 %")
            assertEquals("0.3", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(scaleFormat = null, tooltipFormat = ".4f")
            assertGeneralTooltip(geomLayer, "0.3445")
            assertYAxisTooltip(geomLayer, "0.3445")
            assertEquals("0.3", getYTick(geomLayer))
        }
    }

    @Test
    fun `scale(format) + tooltip format() - the tooltip formatting is applied to the axis tooltip`() {
        run {
            val geomLayer = geomLayer(scaleFormat = "scale = {} %", tooltipFormat = "tooltip = {} %")
            assertGeneralTooltip(geomLayer, "tooltip = 0.34447 %", ::println)  // todo "tooltip = 0.34 %"
            assertYAxisTooltip(geomLayer, "tooltip = 0.34447 %", ::println)    // todo "tooltip = 0.34 %"
            // todo assertEquals("scale = 0.3 %", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(scaleFormat = "scale = {.3f} %", tooltipFormat = "tooltip = {.4f} %")
            assertGeneralTooltip(geomLayer, "tooltip = 0.3445 %")
            assertYAxisTooltip(geomLayer, "tooltip = 0.3445 %")
            assertEquals("scale = 0.300 %", getYTick(geomLayer))
        }
        run {
            val geomLayer = geomLayer(scaleFormat = ".3f", tooltipFormat = ".4f")
            assertGeneralTooltip(geomLayer, "0.3445")
            assertYAxisTooltip(geomLayer, "0.3445")
            assertEquals("0.300", getYTick(geomLayer))
        }
    }

    @Test
    fun `tooltip format() for the variable`() {
        run {
            val geomLayer =
                geomLayer(scaleFormat = null, tooltipFormat = "tooltip = {} %", useTooltipFormatForVarName = true)
            assertGeneralTooltip(geomLayer, "tooltip = 0.34447 %", ::println) // todo "tooltip = 0.34 %"
            assertYAxisTooltip(geomLayer, "tooltip = 0.34447 %", ::println)   // todo "tooltip = 0.34 %"
            assertEquals("0.3", getYTick(geomLayer))
        }
        run {
            val geomLayer =
                geomLayer(scaleFormat = null, tooltipFormat = "tooltip = {.4f} %", useTooltipFormatForVarName = true)
            assertGeneralTooltip(geomLayer, "tooltip = 0.3445 %")
            assertYAxisTooltip(geomLayer, "tooltip = 0.3445 %")
            assertEquals("0.3", getYTick(geomLayer))
        }
        run {
            // add variable to tooltip line
            val geomLayer = geomLayer(
                scaleFormat = null,
                tooltipFormat = "tooltip = {} %",
                useVarNameInTooltip = true,
                useTooltipFormatForVarName = true
            )
            assertGeneralTooltip(geomLayer, "tooltip = 0.34447 %")
            assertYAxisTooltip(geomLayer, "tooltip = 0.34447 %", ::println)  // todo "tooltip = 0.34 %"
            assertEquals("0.3", getYTick(geomLayer))
        }
    }

    @Test
    fun log10() {
        fun log10(scaleFormat: String?, tooltipFormat: String?) = geomLayer(
            additionalScaleOption = Scale.CONTINUOUS_TRANSFORM to "log10",
            scaleFormat = scaleFormat,
            tooltipFormat = tooltipFormat
        )

        val closedRange = DoubleSpan(-0.5, -0.5)

        run {
            val geomLayer = log10(scaleFormat = null, tooltipFormat = null)
            assertGeneralTooltip(geomLayer, "0.344")
            assertYAxisTooltip(geomLayer, "0.344")
            assertEquals("0.32", getYTick(geomLayer, closedRange))
        }
        run {
            val geomLayer = log10(
                scaleFormat = "scale = {} %",     // todo substitute with the default -> 0.32
                tooltipFormat = "tooltip = {} %"  // todo substitute with the default -> 0.344
            )
            assertGeneralTooltip(geomLayer, "tooltip = 0.34447 %", ::println)  // todo "tooltip = 0.344 %"
            assertYAxisTooltip(geomLayer, "tooltip = 0.34447 %", ::println)    // todo "tooltip = 0.344 %"
            areEqual(
                "scale = 0.31622776601683794 %",
                getYTick(geomLayer, closedRange),
                "y tick",
                ::println
            ) // todo "scale = 0.32 %"
        }
        run {
            val geomLayer = log10(scaleFormat = "scale = {.3f} %", tooltipFormat = "tooltip = {.4f} %")
            assertGeneralTooltip(geomLayer, "tooltip = 0.3445 %")
            assertYAxisTooltip(geomLayer, "tooltip = 0.3445 %")
            assertEquals("scale = 0.316 %", getYTick(geomLayer, closedRange))
        }
        run {
            val geomLayer = log10(scaleFormat = ".3f", tooltipFormat = ".4f")
            assertGeneralTooltip(geomLayer, "0.3445")
            assertYAxisTooltip(geomLayer, "0.3445")
            assertEquals("0.316", getYTick(geomLayer, closedRange))
        }
    }

    @Test
    fun dateTime() {
        val instants = List(3) {
            DateTime(Date(1, Month.JANUARY, 2021)).add(Duration.WEEK.mul(it.toLong()))
        }.map { TimeUtil.asInstantUTC(it).toDouble() }
        val dtData = mapOf("date" to instants, "v" to listOf(0, 1, 2))
        val dtMapping = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "v",
            org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "date"
        )
        val closedRange = DoubleSpan(instants.first(), instants.last())

        fun dtLayer(scaleFormat: String?, tooltipFormat: String?): GeomLayer = geomLayer(
            dtData,
            dtMapping,
            additionalScaleOption = Scale.DATE_TIME to true,
            scaleFormat = scaleFormat,
            tooltipFormat = tooltipFormat
        )

        run {
            val geomLayer = dtLayer(scaleFormat = null, tooltipFormat = null)
            assertGeneralTooltip(geomLayer, "00:00")
            assertYAxisTooltip(geomLayer, "00:00")
            assertEquals("Jan 7", getYTick(geomLayer, closedRange))
        }
        run {
            val geomLayer = dtLayer(scaleFormat = "%b %Y", tooltipFormat = "%b %Y")
            assertGeneralTooltip(geomLayer, "Jan 2021")
            assertYAxisTooltip(geomLayer, "Jan 2021")
            assertEquals("Jan 2021", getYTick(geomLayer, closedRange))
        }
        run {
            val geomLayer = dtLayer(scaleFormat = "scale = {}", tooltipFormat = "tooltip = {}")
            //todo assertGeneralTooltip(geomLayer, "tooltip = 00:00")
            //todo assertYAxisTooltip(geomLayer, "tooltip = 00:00")
            //todo assertEquals("scale = Jan 7", getYTick(geomLayer, closedRange))
        }
        run {
            val geomLayer = dtLayer(scaleFormat = "scale = {%b %Y}", tooltipFormat = "tooltip = {%b %Y}")
            //todo assertGeneralTooltip(geomLayer, "tooltip = Jan 2021")
            //todo assertYAxisTooltip(geomLayer, "tooltip = Jan 2021")
            //todo assertEquals("scale = Jan 2021", getYTick(geomLayer, closedRange))
        }
    }

    companion object {
        private fun areEqual(expected: String, actual: String?, name: String, method: (String) -> Unit) {
            if (expected != actual) {
                method("$name:\n\texpected: \"$expected\";\n\tactual: \"$actual\"")
            }
        }

        private fun assertGeneralTooltip(geomLayer: GeomLayer, expected: String, method: (String) -> Unit = ::fail) {
            val ctx = TestingPlotContext.create(geomLayer)
            val dataPoints = geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            val generalTooltip = dataPoints
                .filterNot(LineSpec.DataPoint::isSide)
                .map(LineSpec.DataPoint::value)
                .firstOrNull()
            areEqual(expected, generalTooltip, "general tooltip", method)
        }

        private fun assertYAxisTooltip(geomLayer: GeomLayer, expected: String, method: (String) -> Unit = ::fail) {
            val ctx = TestingPlotContext.create(geomLayer)
            val dataPoints = geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            val yAxisTooltip = dataPoints
                .filter(LineSpec.DataPoint::isAxis)
                .filter { it.aes == org.jetbrains.letsPlot.core.plot.base.Aes.Y }
                .map(LineSpec.DataPoint::value)
                .firstOrNull()
            areEqual(expected, yAxisTooltip, "axis tooltip", method)
        }

        private fun getYTick(geomLayer: GeomLayer, closedRange: DoubleSpan = DoubleSpan(0.3, 0.4)): String {
            return ScaleConfigLabelsTest.getScaleLabels(
                geomLayer.scaleMap.getValue(org.jetbrains.letsPlot.core.plot.base.Aes.Y),
                targetCount = 1,
                closedRange
            ).first()
        }
    }
}