/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import demoAndTestShared.parsePlotSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.core.spec.back.transform.PlotConfigBackendTransforms
import org.jetbrains.letsPlot.core.spec.getBool
import org.jetbrains.letsPlot.core.spec.getString
import kotlin.test.Test

class WaterfallPlotSpecChangeTest {
    @Test
    fun `waterfall_plot(color='flow_type', relative_labels=layer_labels()-inherit_color())`() {
        val spec = """
            |{
            |  "data": {
            |    "cat": [ "A", "B" ],
            |    "val": [ 2.0, -1.0 ]
            |  },
            |  "bistro": {
            |    "name": "waterfall",
            |    "x": "cat",
            |    "y": "val",
            |    "color": "flow_type",
            |    "fill": "gray90",
            |    "size": 0.75,
            |    "relative_labels": {
            |      "formats": [],
            |      "lines": [ "@..dy.." ],
            |      "use_layer_color": true
            |    },
            |    "background_layers": []
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "cat" },
            |      { "type": "int", "column": "val" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [],
            |  "metainfo_list": []
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        val resSpec = PlotConfigBackendTransforms.bistroTransform().apply(plotSpec)

        assertThat(resSpec.getBool("layers", 1, "labels", "use_layer_color")).isTrue()
        assertThat(resSpec.getBool("layers", 2, "labels", "use_layer_color")).isFalse()
    }

    @Test
    fun `waterfall_plot(color='flow_type', label=element_text(color='inherit'))`() {
        val spec = """
            |{
            |    "data": {
            |        "cat": [ "A", "B" ], 
            |        "val": [ 2.0, -1.0 ]
            |    }, 
            |    "bistro": {
            |        "name": "waterfall", 
            |        "x": "cat", 
            |        "y": "val", 
            |        "fill": "gray90", 
            |        "size": 0.75, 
            |        "color": "flow_type",
            |        "relative_labels": {
            |            "formats": [ ], 
            |            "lines": [ "@..dy.." ], 
            |            "use_layer_color": true
            |        }, 
            |        "label": { "color": "inherit", "blank": false }, 
            |        "background_layers": [ ]
            |    }, 
            |    "data_meta": {
            |        "series_annotations": [
            |            { "type": "str", "column": "cat" }, 
            |            { "type": "int", "column": "val" }
            |        ]
            |    }, 
            |    "kind": "plot", 
            |    "scales": [ ], 
            |    "layers": [ ], 
            |    "metainfo_list": [ ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        val resSpec = PlotConfigBackendTransforms.bistroTransform().apply(plotSpec)

        assertThat(resSpec.getBool("layers", 1, "labels", "use_layer_color")).isTrue()
        assertThat(resSpec.getBool("layers", 2, "labels", "use_layer_color")).isTrue()
    }

    @Test
    fun `waterfall_plot(color='flow_type', label=element_text(color='red'), relative_labels=layer_labels()-inherit_color())`() {
        /*
        waterfall_plot({'cat': ['A', 'B'], 'val': [2, -1]}, 'cat', 'val',
               size=.75,
               fill='gray90',
               color='flow_type',
               label=element_text(color='white'),
               relative_labels=layer_labels()
                   .line('@..dy..')
                   .inherit_color()
              )
         */
        val spec = """
            |{
            |  "data": {
            |    "cat": [ "A", "B" ],
            |    "val": [ 2.0, -1.0 ]
            |  },
            |  "bistro": {
            |    "name": "waterfall",
            |    "x": "cat",
            |    "y": "val",
            |    "color": "flow_type",
            |    "fill": "gray90",
            |    "size": 0.75,
            |    "relative_labels": {
            |      "formats": [],
            |      "lines": [ "@..dy.." ],
            |      "use_layer_color": true
            |    },
            |    "label": {
            |      "color": "pink",
            |      "blank": false
            |    },
            |    "background_layers": []
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "cat" },
            |      { "type": "int", "column": "val" }
            |    ]
            |  },
            |  "kind": "plot"
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        val resSpec = PlotConfigBackendTransforms.bistroTransform().apply(plotSpec)

        assertThat(resSpec.getBool("layers", 1, "labels", "use_layer_color")).isTrue()
        assertThat(resSpec.getBool("layers", 2, "labels", "use_layer_color")).isFalse()
    }

    @Test
    fun `waterfall_plot(label='blank')`() {
        // waterfall_plot({'cat': ['A', 'B'], 'val': [2, -1]}, 'cat', 'val', label='blank')
        val spec = """
            |{
            |  "data": {
            |    "cat": [ "A", "B" ],
            |    "val": [ 2.0, -1.0 ]
            |  },
            |  "bistro": {
            |    "name": "waterfall",
            |    "x": "cat",
            |    "y": "val",
            |    "label": "blank",
            |    "background_layers": []
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "cat" },
            |      { "type": "int", "column": "val" }
            |    ]
            |  },
            |  "kind": "plot"
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        val resSpec = PlotConfigBackendTransforms.bistroTransform().apply(plotSpec)

        assertThat(resSpec.getString("layers", 1, "labels")).isEqualTo("none")
        assertThat(resSpec.getString("layers", 2, "labels")).isEqualTo("none")
    }
}