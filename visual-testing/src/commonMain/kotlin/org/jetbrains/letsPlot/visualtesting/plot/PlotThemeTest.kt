/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

class PlotThemeTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestBase() {
    init {
        registerTest(::plot_theme_alphaColorInTitles)
    }

    fun plot_theme_alphaColorInTitles(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Title", "subtitle": "Subtitle" },
            |  "caption": { "text": "Caption" },
            |  "theme": {
            |    "plot_title": { "color": "", "blank": false },
            |    "plot_subtitle": { "color": "#00000000", "blank": false },
            |    "plot_caption": { "color": "rgba(123, 0, 222, 0)", "blank": false }
            |  }
            |}
            """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        return paint(plotCanvasDrawable)
    }
}