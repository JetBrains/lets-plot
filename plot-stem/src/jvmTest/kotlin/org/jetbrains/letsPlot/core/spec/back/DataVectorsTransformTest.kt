/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Meta.KIND
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.PLOT
import org.jetbrains.letsPlot.core.spec.Option.Plot
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(Parameterized::class)
class DataVectorsTransformTest(
    private val myInput: MutableMap<String, Any>,
    private val myExpected: Map<String, Any>,
    private val myIdentityComparison: Boolean
) {
    @Before
    fun setUp() {
        SpecIdGeneration.disable()
    }

    @After
    fun tearDown() {
        SpecIdGeneration.enable()
    }

    @Test
    fun transformed() {
        val transformed = BackendTestUtil.backendSpecTransform(myInput)
        assertEquals(myExpected, transformed)
        if (myIdentityComparison) {
            // This only works for plot data located on the top level
            val expectedData = myExpected[DATA] as Map<*, *>
            val transformedData = transformed[DATA] as Map<*, *>
            assertEquals(expectedData, transformedData)
            for (`var` in expectedData.keys) {
                assertSame(expectedData[`var`], transformedData[`var`])
            }
        }
    }

    companion object {
        private val IDENTICAL_NULL = listOf<Any?>(null, null)
        private val IDENTICAL_ABC = listOf("a", "b", "c")
        private val IDENTICAL_123 = listOf(1.0, 2.0, 3.0)

        private const val IDENTICAL_COMPARISON =
            false  // true - not working data conversion to dataframe and back in PlotConfig

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    plotSpecWithData(listOf(1, 2, 3)),
                    plotSpecWithData(listOf(1.0, 2.0, 3.0)), false
                ),
                arrayOf(
                    plotSpecWithData(listOf(1.0, 2.0, 3)),
                    plotSpecWithData(listOf(1.0, 2.0, 3.0)), false
                ),
                arrayOf(
                    plotSpecWithData(listOf(null, 2, null)),
                    plotSpecWithData(listOf(null, 2.0, null)), false
                ),
                arrayOf(
                    plotSpecWithData(listOf("a", 2, null)),
                    plotSpecWithData(listOf("a", 2.0, null)), false
                ),
                arrayOf(
                    plotSpecWithData(IDENTICAL_NULL),
                    plotSpecWithData(IDENTICAL_NULL), IDENTICAL_COMPARISON
                ),
                arrayOf(
                    plotSpecWithData(IDENTICAL_ABC),
                    plotSpecWithData(IDENTICAL_ABC), IDENTICAL_COMPARISON
                ),
                arrayOf(
                    plotSpecWithData(IDENTICAL_123),
                    plotSpecWithData(IDENTICAL_123), IDENTICAL_COMPARISON
                ),
                arrayOf(
                    plotSpecWithLayerData(listOf(1, 2, 3)),
                    plotSpecWithLayerData(listOf(1.0, 2.0, 3.0)), false
                ),
                arrayOf(
                    plotSpecWithLayerData(listOf<Number>(1.0, 2.0, 3)),
                    plotSpecWithLayerData(listOf(1.0, 2.0, 3.0)), false
                ),
                arrayOf(
                    plotSpecWithLayerData(listOf(null, 2, null)),
                    plotSpecWithLayerData(listOf(null, 2.0, null)), false
                ),
                arrayOf(
                    plotSpecWithLayerData(listOf("a", 2, null)),
                    plotSpecWithLayerData(listOf("a", 2.0, null)), false
                )
            )
        }

        private fun dataSpec(varName: String, list: List<*>): Map<String, Any> {
            return mapOf(
                varName to list
            )
        }

        private fun layerSpec(data: Map<*, *>): Map<String, Any> {
            return mapOf(
                Layer.GEOM to "point",
                DATA to data,
                MAPPING to mappingFor(data)
            )
        }

        private fun mappingFor(data: Map<*, *>): Map<*, *> {
            val spec = HashMap<Any, Any>()
            for (variable in data.keys) {
                spec[variable!!] = variable
            }
            return spec
        }

        private fun plotSpec(data: Map<*, *>, layerSpec: Map<*, *>): Map<String, Any> {
            return mapOf(
                KIND to PLOT,
                DATA to data,
                MAPPING to mappingFor(data),
                Plot.LAYERS to listOf(layerSpec)
            )
        }

        private fun plotSpecWithData(data: List<*>): Map<String, Any> {
            return plotSpec(
                dataSpec("x", data),
                layerSpec(emptyMap<Any, Any>())
            )
        }

        private fun plotSpecWithLayerData(data: List<*>): Map<String, Any> {
            return plotSpec(
                emptyMap<Any, Any>(),
                layerSpec(dataSpec("x", data))
            )
        }
    }
}
