/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class PlotThemeVisualTest : VisualPlotTestBase() {
    @Test
    fun `legend at bottom`() {
        val spec = """
            {
              "data": {
                "x": [
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0
                ]
              },
              "mapping": {
                "x": "x",
                "color": "x"
              },
              "data_meta": {
                "series_annotations": [
                  {
                    "type": "int",
                    "column": "x"
                  }
                ]
              },
              "theme": {
                "legend_position": "bottom"
              },
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "point",
                  "mapping": {},
                  "data_meta": {}
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        assertPlot("plot_legend_at_bottom_test.png", parsePlotSpec(spec))
    }

    @Test
    fun `plot title with blank line`() {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "ggtitle": {
                "text": "A\n\nB"
              },
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "blank",
                  "mapping": {},
                  "inherit_aes": false,
                  "tooltips": "none",
                  "data_meta": {}
                }
              ],
              "metainfo_list": []
            }
        """.trimIndent()

        assertPlot("plot_title_with_blank_line_test.png", parsePlotSpec(spec))
    }
}
