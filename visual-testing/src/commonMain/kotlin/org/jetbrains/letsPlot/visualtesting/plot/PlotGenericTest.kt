/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

class PlotGenericTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestSuitBase() {

    init {
        registerTest(::annotation_raster_under_points)
        registerTest(::annotation_raster_over_points)
        registerTest(::annotation_raster_default_bounds)
        registerTest(::annotation_raster_partial_default_bounds)
        registerTest(::annotation_raster_inner_bounds)
        registerTest(::annotation_raster_outside_bounds)
    }

    fun annotation_raster_under_points(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 320.0, "height": 260.0 },
            |  "data": {
            |    "x": [ 1.0, 1.5, 2.0 ],
            |    "y": [ 1.0, 1.5, 2.0 ]
            |  },
            |  "mapping": { "x": "x", "y": "y" },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 1.0, 2.0 ],
            |    "ylim": [ 1.0, 2.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "annotation_raster",
            |      "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAANElEQVR4nGN4757/HxsW7NqEFTMMVw0MDAxgjFUD27UF/9ExTMP/i2kYGKsGEMameLhoAABGcXfsngqr0AAAAABJRU5ErkJggg==",
            |      "interpolate": false,
            |      "inherit_aes": false,
            |      "show_legend": false
            |    },
            |    {
            |      "geom": "point",
            |      "size": 12.0,
            |      "color": "black"
            |    }
            |  ],
            |  "theme": {
            |    "panel_grid": { "blank": true }
            |  }
            |}
        """.trimMargin()

        return paint(parseJson(spec))
    }

    fun annotation_raster_over_points(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 320.0, "height": 260.0 },
            |  "data": {
            |    "x": [ 1.0, 1.5, 2.0 ],
            |    "y": [ 1.0, 1.5, 2.0 ]
            |  },
            |  "mapping": { "x": "x", "y": "y" },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 1.0, 2.0 ],
            |    "ylim": [ 1.0, 2.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "size": 12.0,
            |      "color": "black"
            |    },
            |    {
            |      "geom": "annotation_raster",
            |      "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAANElEQVR4nGN4757/HxsW7NqEFTMMVw0MDAxgjFUD27UF/9ExTMP/i2kYGKsGEMameLhoAABGcXfsngqr0AAAAABJRU5ErkJggg==",
            |      "interpolate": false,
            |      "inherit_aes": false,
            |      "show_legend": false
            |    }
            |  ],
            |  "theme": {
            |    "panel_grid": { "blank": true }
            |  }
            |}
        """.trimMargin()

        return paint(parseJson(spec))
    }

    fun annotation_raster_default_bounds(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 320.0, "height": 260.0 },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 0.0, 10.0 ],
            |    "ylim": [ 0.0, 10.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "annotation_raster",
            |      "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAANElEQVR4nGN4757/HxsW7NqEFTMMVw0MDAxgjFUD27UF/9ExTMP/i2kYGKsGEMameLhoAABGcXfsngqr0AAAAABJRU5ErkJggg==",
            |      "interpolate": false,
            |      "inherit_aes": false,
            |      "show_legend": false
            |    }
            |  ],
            |  "theme": {
            |    "panel_grid": { "blank": true }
            |  }
            |}
        """.trimMargin()

        return paint(parseJson(spec))
    }

    fun annotation_raster_partial_default_bounds(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 320.0, "height": 260.0 },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 0.0, 10.0 ],
            |    "ylim": [ 0.0, 10.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "annotation_raster",
            |      "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAANElEQVR4nGN4757/HxsW7NqEFTMMVw0MDAxgjFUD27UF/9ExTMP/i2kYGKsGEMameLhoAABGcXfsngqr0AAAAABJRU5ErkJggg==",
            |      "interpolate": false,
            |      "inherit_aes": false,
            |      "show_legend": false,
            |      "xmin": 2.0,
            |      "xmax": 8.0,
            |      "ymax": 7.0
            |    }
            |  ],
            |  "theme": {
            |    "panel_grid": { "blank": true }
            |  }
            |}
        """.trimMargin()

        return paint(parseJson(spec))
    }

    fun annotation_raster_inner_bounds(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 320.0, "height": 260.0 },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 0.0, 10.0 ],
            |    "ylim": [ 0.0, 10.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "annotation_raster",
            |      "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAANElEQVR4nGN4757/HxsW7NqEFTMMVw0MDAxgjFUD27UF/9ExTMP/i2kYGKsGEMameLhoAABGcXfsngqr0AAAAABJRU5ErkJggg==",
            |      "interpolate": false,
            |      "inherit_aes": false,
            |      "show_legend": false,
            |      "xmin": 2.0,
            |      "xmax": 8.0,
            |      "ymin": 2.0,
            |      "ymax": 8.0
            |    }
            |  ],
            |  "theme": {
            |    "panel_grid": { "blank": true }
            |  }
            |}
        """.trimMargin()

        return paint(parseJson(spec))
    }

    fun annotation_raster_outside_bounds(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 320.0, "height": 260.0 },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 0.0, 10.0 ],
            |    "ylim": [ 0.0, 10.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "annotation_raster",
            |      "href": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAANElEQVR4nGN4757/HxsW7NqEFTMMVw0MDAxgjFUD27UF/9ExTMP/i2kYGKsGEMameLhoAABGcXfsngqr0AAAAABJRU5ErkJggg==",
            |      "interpolate": false,
            |      "inherit_aes": false,
            |      "show_legend": false,
            |      "xmin": -2.0,
            |      "xmax": 12.0,
            |      "ymin": -2.0,
            |      "ymax": 12.0
            |    }
            |  ],
            |  "theme": {
            |    "panel_grid": { "blank": true }
            |  }
            |}
        """.trimMargin()

        return paint(parseJson(spec))
    }
}
