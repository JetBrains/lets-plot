/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_CONNECTOR
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_H_LINE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL_FORMAT
import org.junit.Test
import kotlin.test.assertNotNull

class OptionsBuilderTest {
    @Test
    fun simple() {
        val plotOptions = WaterfallPlotOptionsBuilder(
            data = mapOf(
                "X" to listOf("A", "B", "T1", "A", "B", "T2"),
                "Y" to listOf(2.0, -1.0, null, 2.0, -1.0, null),
                "M" to listOf("relative", "relative", "total", "relative", "relative", "total")
            ),
            x = "X",
            y = "Y",
            measure = "M",
            color = null,
            fill = null,
            size = null,
            alpha = null,
            lineType = null,
            width = 0.9,
            showLegend = false,
            relativeTooltipsOptions = null,
            absoluteTooltipsOptions = null,
            calcTotal = true,
            totalTitle = null,
            sortedValue = false,
            threshold = null,
            maxValues = null,
            hLineOptions = DEF_H_LINE,
            hLineOnTop = false,
            connectorOptions = DEF_CONNECTOR,
            labelOptions = DEF_LABEL,
            labelFormat = DEF_LABEL_FORMAT,
            group = null
        ).build()
        val relativeBoxOptions = plotOptions.layerOptions!!.first { it.geom == GeomKind.CROSS_BAR }
        assertNotNull(relativeBoxOptions)
        relativeBoxOptions.data!!.let {
            assertThat(it["..x.."]).isEqualTo(listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            assertThat(it["..xlabel.."]).isEqualTo(listOf("A", "B", "T1", "A", "B", "T2"))
            assertThat(it["..ymin.."]).isEqualTo(listOf(0.0, 1.0, null, 1.0, 2.0, null))
            assertThat(it["..ymiddle.."]).isEqualTo(listOf(1.0, 1.5, null, 2.0, 2.5, null))
            assertThat(it["..ymax.."]).isEqualTo(listOf(2.0, 2.0, null, 3.0, 3.0, null))
            assertThat(it["..measure.."]).isEqualTo(listOf("relative", "relative", "total", "relative", "relative", "total"))
            assertThat(it["..flow_type.."]).isEqualTo(listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"))
            assertThat(it["..initial.."]).isEqualTo(listOf(0.0, 2.0, 0.0, 1.0, 3.0, 0.0))
            assertThat(it["..value.."]).isEqualTo(listOf(2.0, 1.0, 1.0, 3.0, 2.0, 2.0))
            assertThat(it["..dy.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0))
            assertThat(it["..label.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 2.0))
            (listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.0) zip it["..radius.."]!!).forEach { (expected, actual) ->
                assertThat(actual as Double).isEqualTo(expected, Offset.offset(1e-16))
            }
        }
        val absoluteBoxOptions = plotOptions.layerOptions!!.last { it.geom == GeomKind.CROSS_BAR }
        assertNotNull(absoluteBoxOptions)
        absoluteBoxOptions.data!!.let {
            assertThat(it["..x.."]).isEqualTo(listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            assertThat(it["..xlabel.."]).isEqualTo(listOf("A", "B", "T1", "A", "B", "T2"))
            assertThat(it["..ymin.."]).isEqualTo(listOf(null, null, 0.0, null, null, 0.0))
            assertThat(it["..ymiddle.."]).isEqualTo(listOf(null, null, 0.5, null, null, 1.0))
            assertThat(it["..ymax.."]).isEqualTo(listOf(null, null, 1.0, null, null, 2.0))
            assertThat(it["..measure.."]).isEqualTo(listOf("relative", "relative", "total", "relative", "relative", "total"))
            assertThat(it["..flow_type.."]).isEqualTo(listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"))
            assertThat(it["..initial.."]).isEqualTo(listOf(0.0, 2.0, 0.0, 1.0, 3.0, 0.0))
            assertThat(it["..value.."]).isEqualTo(listOf(2.0, 1.0, 1.0, 3.0, 2.0, 2.0))
            assertThat(it["..dy.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0))
            assertThat(it["..label.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 2.0))
            (listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.0) zip it["..radius.."]!!).forEach { (expected, actual) ->
                assertThat(actual as Double).isEqualTo(expected, Offset.offset(1e-16))
            }
        }
    }

    @Test
    fun grouping() {
        val plotOptions = WaterfallPlotOptionsBuilder(
            data = mapOf(
                "X" to listOf("A", "B", "T1", "A", "B", "T2"),
                "Y" to listOf(2.0, -1.0, null, 2.0, -1.0, null),
                "M" to listOf("relative", "relative", "total", "relative", "relative", "total"),
                "G" to listOf("a", "a", "a", "b", "b", "b")
            ),
            x = "X",
            y = "Y",
            measure = "M",
            group = "G",
            color = null,
            fill = null,
            size = null,
            alpha = null,
            lineType = null,
            width = 0.9,
            showLegend = false,
            relativeTooltipsOptions = null,
            absoluteTooltipsOptions = null,
            calcTotal = true,
            totalTitle = null,
            sortedValue = false,
            threshold = null,
            maxValues = null,
            hLineOptions = DEF_H_LINE,
            hLineOnTop = false,
            connectorOptions = DEF_CONNECTOR,
            labelOptions = DEF_LABEL,
            labelFormat = DEF_LABEL_FORMAT
        ).build()

        val relativeBoxOptions = plotOptions.layerOptions!!.first { it.geom == GeomKind.CROSS_BAR }
        relativeBoxOptions.data!!.let {
            assertThat(it["..x.."]).isEqualTo(listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            assertThat(it["..xlabel.."]).isEqualTo(listOf("A", "B", "T1", "A", "B", "T2"))
            assertThat(it["..ymin.."]).isEqualTo(listOf(0.0, 1.0, null, 0.0, 1.0, null))
            assertThat(it["..ymiddle.."]).isEqualTo(listOf(1.0, 1.5, null, 1.0, 1.5, null))
            assertThat(it["..ymax.."]).isEqualTo(listOf(2.0, 2.0, null, 2.0, 2.0, null))
            assertThat(it["..measure.."]).isEqualTo(listOf("relative", "relative", "total", "relative", "relative", "total"))
            assertThat(it["..flow_type.."]).isEqualTo(listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"))
            assertThat(it["..initial.."]).isEqualTo(listOf(0.0, 2.0, 0.0, 0.0, 2.0, 0.0))
            assertThat(it["..value.."]).isEqualTo(listOf(2.0, 1.0, 1.0, 2.0, 1.0, 1.0))
            assertThat(it["..dy.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0))
            assertThat(it["..label.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0))
            (listOf(0.1, 0.1, 0.0, 0.1, 0.1, 0.0) zip it["..radius.."]!!).forEach { (expected, actual) ->
                assertThat(actual as Double).isEqualTo(expected, Offset.offset(1e-16))
            }
        }
        val absoluteBoxOptions = plotOptions.layerOptions!!.last { it.geom == GeomKind.CROSS_BAR }
        absoluteBoxOptions.data!!.let {
            assertThat(it["..x.."]).isEqualTo(listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
            assertThat(it["..xlabel.."]).isEqualTo(listOf("A", "B", "T1", "A", "B", "T2"))
            assertThat(it["..ymin.."]).isEqualTo(listOf(null, null, 0.0, null, null, 0.0))
            assertThat(it["..ymiddle.."]).isEqualTo(listOf(null, null, 0.5, null, null, 0.5))
            assertThat(it["..ymax.."]).isEqualTo(listOf(null, null, 1.0, null, null, 1.0))
            assertThat(it["..measure.."]).isEqualTo(listOf("relative", "relative", "total", "relative", "relative", "total"))
            assertThat(it["..flow_type.."]).isEqualTo(listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"))
            assertThat(it["..initial.."]).isEqualTo(listOf(0.0, 2.0, 0.0, 0.0, 2.0, 0.0))
            assertThat(it["..value.."]).isEqualTo(listOf(2.0, 1.0, 1.0, 2.0, 1.0, 1.0))
            assertThat(it["..dy.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0))
            assertThat(it["..label.."]).isEqualTo(listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0))
            (listOf(0.1, 0.1, 0.0, 0.1, 0.1, 0.0) zip it["..radius.."]!!).forEach { (expected, actual) ->
                assertThat(actual as Double).isEqualTo(expected, Offset.offset(1e-16))
            }
        }

        val scaleXOptions = plotOptions.scaleOptions!!.first { it.aes == Aes.X }
        assertThat(scaleXOptions.breaks).isEqualTo(listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
        assertThat(scaleXOptions.labels).isEqualTo(listOf("A", "B", "T1", "A", "B", "T2"))
    }
}