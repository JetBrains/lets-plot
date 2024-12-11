/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.parsePlotSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.encoding.UnsupportedRGBEncoder
import org.jetbrains.letsPlot.core.util.PlotSvgExportCommon
import org.junit.Test

class SeriesAnnotationTypeTest {

    @Test
    fun `ggplot(data) + geom_label(aes(label='t')) + geom_label(aes(label='f')) + geom_label(aes(label='i'))`() {
        val spec = """
            |{
            |  "data": {
            |    "t": [1704108896000.0],
            |    "f": [12345.1],
            |    "i": [54321.1]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "column": "t", "type": "datetime" },
            |      { "column": "f", "type": "float" },
            |      { "column": "i", "type": "int" }
            |    ]
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "label",
            |      "mapping": { "label": "t" },
            |      "x": 0.0,
            |      "y": 0.0
            |    },
            |    {
            |      "geom": "label",
            |      "mapping": { "label": "f" },
            |      "x": 0.0,
            |      "y": 1.0
            |    },
            |    {
            |      "geom": "label",
            |      "mapping": { "label": "i" },
            |      "x": 0.0,
            |      "y": 2.0
            |    }
            |  ]
            |}
        """.trimMargin()


        PlotSvgExportCommon.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = UnsupportedRGBEncoder,
            useCssPixelatedImageRendering = false
        ).let { svg ->
            assertThat(svg).contains("<tspan>54,321.1</tspan>")
            assertThat(svg).contains("<tspan>12,345.1</tspan>")
            assertThat(svg).contains("<tspan>2024-01-01T11:34:56</tspan>")
        }
    }

    @Test
    fun `ggplot(foo_int) + geom_label(aes(label='foo'), data=foo_date) + geom_label(aes(label='foo'), data=foo_float) + geom_label(aes(label='foo'))`() {
        val spec = """
            |{
            |  "data": { "foo": [54321.1] },
            |  "data_meta": { "series_annotations": [ { "column": "foo", "type": "int" } ] },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "label",
            |      "data": { "foo": [1704108896000.0] },
            |      "data_meta": { "series_annotations": [ { "column": "foo", "type": "datetime" } ] },
            |      "mapping": { "label": "foo" },
            |      "x": 0.0,
            |      "y": 0.0
            |    },
            |    {
            |      "geom": "label",
            |      "data": { "foo": [12345.1] },
            |      "data_meta": { "series_annotations": [ { "column": "foo", "type": "float" } ] },
            |      "mapping": { "label": "foo" },
            |      "x": 0.0,
            |      "y": 1.0
            |    },
            |    {
            |      "geom": "label",
            |      "mapping": { "label": "foo" },
            |      "x": 0.0,
            |      "y": 2.0
            |    }
            |  ]
            |}
        """.trimMargin()


        PlotSvgExportCommon.buildSvgImageFromRawSpecs(
            plotSpec = parsePlotSpec(spec),
            rgbEncoder = UnsupportedRGBEncoder,
            useCssPixelatedImageRendering = false
        ).let { svg ->
            assertThat(svg).contains("<tspan>54,321.1</tspan>")
            assertThat(svg).contains("<tspan>12,345.1</tspan>")
            assertThat(svg).contains("<tspan>2024-01-01T11:34:56</tspan>")
        }
    }
}
