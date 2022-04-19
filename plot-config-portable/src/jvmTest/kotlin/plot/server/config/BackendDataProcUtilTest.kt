/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.config.DataMetaUtil
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.parsePlotSpec
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Testing layer's property: orientation = "y"
 *
 * See related: YOrientationBatik.kt demo.
 */
class BackendDataProcUtilTest {

    @Test
    fun test() {
        check(plotSpecs_basic(), expected_basic())
        check(plotSpecs_sortedAlphabeticallyRevesed(), expected_sortedAlphabeticallyRevesed())
        check(plotSpecs_sortedByCount(), expected_sortedByCount())

        check(plotSpecs_basic(yOrientation = true), expected_basic())
    }

    private fun check(plotSpec: MutableMap<String, Any>, expected: Expected) {
        // See related: TestUtil.createGeomLayers()
        val layerConfig = ServerSideTestUtil.createLayerConfigs(plotSpec)[0]

        val dataAfterStat = DataFrameUtil.toMap(layerConfig.ownData!!)
        assertEquals(expected.data, dataAfterStat)

        val layerOptions = layerConfig.mergedOptions
        val dataMeta = layerOptions.getValue(DATA_META) as Map<*, *>
        if (expected.asDiscreteAesSet.isNotEmpty()) {
            val asDiscreteAesSet = DataMetaUtil.getAsDiscreteAesSet(dataMeta)
            assertEquals(expected.asDiscreteAesSet, asDiscreteAesSet)
        } else {
            assertTrue(dataMeta.isEmpty())
        }


        val orderOptions = layerConfig.orderOptions
        expected.orderOption?.let {
            assertEquals(1, orderOptions.size)
            assertEquals(it, orderOptions[0])
        } ?: run {
            assertTrue(orderOptions.isEmpty())
        }
    }

    class Expected(
        val data: Map<String, Any>,
        val asDiscreteAesSet: Set<String> = emptySet(),
        val orderOption: OrderOptionUtil.OrderOption? = null
    )

    companion object {
        private const val CATEGORY_VAR = "varCategory"
        private const val GROUP_VAR = "varGroup"
        private const val NUMERIC_VAR = "varNum"

        private val DATA = mapOf(
            CATEGORY_VAR to List(30) { "a" } +
                    List(40) { "b" } +
                    List(20) { "c" },
            GROUP_VAR to listOf("g0") + List(29) { "g1" } +
                    List(39) { "g1" } + listOf("g0") +
                    List(19) { "g1" } + listOf("g0"),
            NUMERIC_VAR to listOf(0) + List(29) { 1 } +
                    List(39) { 1 } + listOf(0) +
                    List(19) { 1 } + listOf(0),
        )

        private val SORT_BY_COUNT_DATA_META = """
            {
                'mapping_annotations': [ 
                    {
                        'aes': 'x',
                        'annotation': 'as_discrete',
                        'parameters': { 
                                'label': '$CATEGORY_VAR',
                                'order_by': '..count..'
                        }
                    }
                ]
            }
        """.trimIndent()

        private val SORT_ALPHABETICALLY_REVERSED_DATA_META = """
            {
                'mapping_annotations': [ 
                    {
                        'aes': 'x',
                        'annotation': 'as_discrete',
                        'parameters': { 
                                'label': '$CATEGORY_VAR',
                                'order': -1
                        }
                    }
                ]
            }
        """.trimIndent()

        private fun createPlotSpec(
            layerMapping: String,
            dataMeta: String = "{}",
            yOrientation: Boolean
        ): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'layers': [
                        {
                            ${if (yOrientation) "'orientation': 'y'," else ""}
                            'geom': 'bar', 
                            'mapping': $layerMapping, 
                            'data_meta': $dataMeta
                        }
                    ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }

        private fun categoryAes(yOrientation:Boolean): String {
            return if(yOrientation) "y" else "x"
        }

        // Basic

        private fun plotSpecs_basic(yOrientation: Boolean = false): MutableMap<String, Any> {
            return createPlotSpec("{'${categoryAes(yOrientation)}': '$CATEGORY_VAR'}", yOrientation = yOrientation)
        }

        private fun expected_basic(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0)
                )
            )
        }

        // Categories are sorted alphabetically

        private fun plotSpecs_sortedAlphabeticallyRevesed(yOrientation: Boolean = false): MutableMap<String, Any> {
            val dataMeta = SORT_ALPHABETICALLY_REVERSED_DATA_META
            return createPlotSpec("{'x': '$CATEGORY_VAR'}", dataMeta, yOrientation = yOrientation)
        }

        private fun expected_sortedAlphabeticallyRevesed(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    DataMetaUtil.toDiscrete(CATEGORY_VAR) to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0)
                ),
                asDiscreteAesSet = setOf("x"),
                orderOption = OrderOptionUtil.OrderOption.create(
                    variableName = DataMetaUtil.toDiscrete(CATEGORY_VAR),
                    orderBy = null,
                    order = -1
                )
            )
        }

        // Categories are sorted by count

        private fun plotSpecs_sortedByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
            val dataMeta = SORT_BY_COUNT_DATA_META
            return createPlotSpec("{'x': '$CATEGORY_VAR'}", dataMeta, yOrientation = yOrientation)
        }

        private fun expected_sortedByCount(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    DataMetaUtil.toDiscrete(CATEGORY_VAR) to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0)
                ),
                asDiscreteAesSet = setOf("x"),
                orderOption = OrderOptionUtil.OrderOption.create(
                    variableName = DataMetaUtil.toDiscrete(CATEGORY_VAR),
                    orderBy = "..count..",
                    order = null
                )
            )
        }
    }
}
