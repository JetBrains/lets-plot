/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.assertion.assertDoesNotFail
import jetbrains.datalore.plot.DemoAndTest
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test


class KeepVariablesUsedInConfigTest {
    private val data = """{ "x": [0, 0],  "g": ['a', 'b'] }"""

    @Test
    fun `keep tooltip's variable after stat - settings in plot`() {
        val spec = """{
            "kind": "plot",
            "data": $data,
            "mapping": { "x": "x" },
            "layers": [
                {   
                    "geom": "bar",
                    "tooltips": { "tooltip_lines": [ "@g" ] }
                }
            ]
        }""".trimIndent()

        assertDoesNotFail { DemoAndTest.createPlot(parsePlotSpec(spec)) }
    }

    @Test
    fun `keep tooltip's variable after stat - settings in layer`() {
        val spec = """
          {
            "kind": "plot",
            "layers": [
              {
                "geom": "bar",
                "data": $data,
                "mapping": { "x": "x" },
                "tooltips": { "tooltip_lines": [ "@g" ] }
              }
            ]
        }""".trimIndent()

        assertDoesNotFail { DemoAndTest.createPlot(parsePlotSpec(spec)) }
    }

    @Test
    fun `keep ordering variable`() {
        val spec = """
          {
            "kind": "plot",
            "data": $data,
            "mapping": { "x": "x" },
            "layers": [
              {
                "geom": "bar",
                 "data_meta": {
                   "mapping_annotations": [
                       {
                            "aes": "x",
                            "annotation": "as_discrete",
                            "parameters": {
                                "order_by": "g"
                            }
                       }
                   ]
                }
              }
            ]
        }""".trimIndent()

        assertDoesNotFail { DemoAndTest.createPlot(parsePlotSpec(spec)) }
    }
}