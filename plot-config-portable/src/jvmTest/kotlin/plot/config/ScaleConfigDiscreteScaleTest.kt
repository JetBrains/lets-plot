/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.common.color.ColorPalette
import jetbrains.datalore.plot.common.color.ColorScheme
import jetbrains.datalore.plot.common.color.PaletteUtil
import jetbrains.datalore.plot.common.data.SeriesUtil
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
internal class ScaleConfigDiscreteScaleTest(
    private val breaks: List<String>?,      // input data type: Str.
    private val labels: List<String>?,
    private val limits: List<String>?,
    private val outpuValues: List<String>?, // Manually configured output values.
    private val expectedBreaks: List<String>,
    private val expectedLabels: List<String>,
    private val expectedBreakColors: List<Color>, // Let's use color scale.
    private val expectedDataPointColors: List<Color>,
) {

    @Test
    fun verify() {
        // Configure plot.
        val data = mapOf(
//            "value" to listOf(0),
            "cat" to CAT_DATA
        )
        val mapping = mapOf(
//            Aes.X.name to "value",
//            Aes.Y.name to "value",
            Aes.COLOR.name to "cat",
        )

        val scales = listOf(
            mapOf(
                Option.Scale.AES to Aes.COLOR.name,
                Option.Scale.BREAKS to breaks,
                Option.Scale.LABELS to labels,
                Option.Scale.LIMITS to limits,
                Option.Scale.OUTPUT_VALUES to outpuValues,
            )
        )

        val geomLayer = TestUtil.buildPointLayer(data, mapping, scales = scales)
        val scale = geomLayer.scaleMap.getValue(Aes.COLOR)
        val scaleBreaks = scale.getScaleBreaks()
        val scaleMapper = geomLayer.scaleMappersNP.getValue(Aes.COLOR)
        val mappedBreaks = scaleBreaks.transformedValues.map { scaleMapper(it) }

        assertEquals(expectedLabels, scaleBreaks.labels)
        assertEquals(expectedBreaks, scaleBreaks.domainValues)
        assertEquals(expectedBreakColors, mappedBreaks, "[Break Colors]")
        assertEquals(
            expectedDataPointColors,
            ScaleUtil.applyTransform(CAT_DATA, scale.transform).map { scaleMapper(it) },
            "[Data Point Colors]"
        )
    }

    companion object {
        private val CAT_DATA = listOf("A", "B", "C")
        private val NA_COLOR = ColorMapper.NA_VALUE

        @JvmStatic
        @Parameterized.Parameters
        fun params(): List<Array<Any?>> {
            return listOf(
                testParams(),
                testParams(
                    breaks = listOf("B"),
                    expectedBreaks = listOf("B"),
                    expectedLabels = listOf("B"),
                    expectedBreakColors = getColors(CAT_DATA.size, listOf(0)),
                    expectedDataPointColors = getColors(
                        CAT_DATA.size,
                        listOf(1, 0, 2)
                    )  // data: A, B, C. Index after reordering: 1, 0, 2  : (B, A, C). A, C - hidden
                ),
                testParams(
                    limits = listOf("B"),
                    expectedBreaks = listOf("B"),
                    expectedLabels = listOf("B"),
                    expectedBreakColors = getColors(CAT_DATA.size, listOf(0)),
                    expectedDataPointColors = listOf(
                        NA_COLOR,
                        getColors(CAT_DATA.size)[0],
                        NA_COLOR
                    )
                ),
                testParams(
                    limits = listOf("B", "C", "A"),  // reorder
                    expectedBreaks = listOf("B", "C", "A"),
                    expectedLabels = listOf("B", "C", "A"),
//                    expectedBreakColors = // colors order is not affected
                    expectedDataPointColors = getColors(
                        CAT_DATA.size,
                        listOf(2, 0, 1)
                    )  // data: A, B, C. Index after reordering: 2, 0, 1
                ),
                testParams(
                    limits = listOf("B", "C", "A"),  // reorder
                    labels = listOf("A-lab", "B-lab", "C-lab"), // labels without "defined" breaks.
                    expectedBreaks = listOf("B", "C", "A"),
                    expectedLabels = listOf("A-lab", "B-lab", "C-lab"), // limits do not change the labels order
//                    expectedBreakColors = // colors order is not affected
                    expectedDataPointColors = getColors(
                        CAT_DATA.size,
                        listOf(2, 0, 1)
                    )  // data: A, B, C. Index after reordering: 2, 0, 1
                ),
                testParams(
                    outputColors = listOf("red", "green", "blue"),
                    expectedBreakColors = listOf(Color.RED, Color.GREEN, Color.BLUE),
                    expectedDataPointColors = listOf(Color.RED, Color.GREEN, Color.BLUE)
                ),
                testParams(
                    outputColors = listOf("red", "green"),  // less than the data size (3).
                    expectedBreakColors = listOf(Color.RED, Color.GREEN, Color.RED),
                    expectedDataPointColors = listOf(Color.RED, Color.GREEN, Color.RED)
                ),
                testParams(
                    breaks = listOf("B", "E"), // break "E" is not present in data.
                    expectedBreaks = listOf("B", "E"),
                    expectedLabels = listOf("B", "E"),
                    expectedBreakColors = getColors(CAT_DATA.size + 1, listOf(0, 1)), // breaks - first
                    expectedDataPointColors = getColors(
                        CAT_DATA.size + 1,
                        listOf(2, 0, 3)
                    ) // data: A, B, C. Index after reordering: 2, 0, 3  : (B, E, A, C). A, C - hidden
                ),
                testParams(
                    breaks = listOf("E", "B"), // break "E" is not present in data.
                    expectedBreaks = listOf("E", "B"),
                    expectedLabels = listOf("E", "B"),
                    expectedBreakColors = getColors(CAT_DATA.size + 1, listOf(0, 1)), // breaks - first
                    expectedDataPointColors = getColors(
                        CAT_DATA.size + 1,
                        listOf(2, 1, 3)
                    ) // data: A, B, C. Index after reordering: 2, 1, 3  : (E, B, A, C). A, C - hidden
                ),
            )
        }

        private fun testParams(
            breaks: List<String>? = null,      // input data type: Str.
            labels: List<String>? = null,
            limits: List<String>? = null,
            outputColors: List<String>? = null, // Manually configured output colors.
            expectedBreaks: List<String> = CAT_DATA,
            expectedLabels: List<String> = CAT_DATA,
            expectedBreakColors: List<Color> = getColors(CAT_DATA.size),
            expectedDataPointColors: List<Color> = getColors(CAT_DATA.size)
        ): Array<Any?> {
            return arrayOf(
                breaks, labels, limits, outputColors,
                expectedBreaks, expectedLabels, expectedBreakColors, expectedDataPointColors
            )
        }

        private fun scaleCfg(
            breaks: List<String>? = null,      // input data type: Str.
            labels: List<String>? = null,
            limits: List<String>? = null,
            outpuValues: List<String>? = null, // Manually configured output values.
        ): List<Any?> {
            return listOf(breaks, labels, limits, outpuValues)
        }

        private fun expectations(
            breaks: List<String>,
            labels: List<String>,
            output: List<Color> = getColors(CAT_DATA.size)
        ): List<Any?> {
            return listOf(breaks, labels, output)
        }

        private fun getColors(count: Int, pickAt: List<Int>? = null): List<Color> {
            // Default palette type: brewer 'Set2'
            val colorScheme: ColorScheme = ColorPalette.Qualitative.Set2
            val colors = PaletteUtil.schemeColors(colorScheme, count)
            return pickAt?.let { SeriesUtil.pickAtIndices(colors, pickAt) } ?: colors
        }
    }
}