package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.ImageComparer
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.util.PlotExportCommon

expect fun createImageComparer(fonts: List<String>): ImageComparer

expect fun assertPlot(
    imageComparer: ImageComparer,
    expectedFileName: String,
    plotSpec: MutableMap<String, Any>,
    width: Number? = null,
    height: Number? = null,
    unit: PlotExportCommon.SizeUnit? = null,
    dpi: Number? = null,
    scale: Number? = null
)

internal fun MutableMap<String, Any>.themeTextNotoSans(): MutableMap<String, Any> {
    val theme = getMap("theme") ?: emptyMap()
    this[Option.Plot.THEME] =  theme + mapOf(
        "text" to mapOf(
            "blank" to false,
            "family" to "Noto Sans"
        ),
        "axis_title_y" to mapOf(
            "blank" to true // hide rotated text - antialiasing may cause image differences
        )
    )
    return this
}

