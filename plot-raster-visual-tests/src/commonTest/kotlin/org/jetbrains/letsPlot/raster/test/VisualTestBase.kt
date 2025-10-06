package org.jetbrains.letsPlot.raster.test

import org.jetbrains.letsPlot.core.util.PlotExportCommon
import kotlin.test.BeforeTest

open class VisualTestBase {
    val fonts = mutableListOf<String>()

    protected fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Number? = null,
        height: Number? = null,
        unit: PlotExportCommon.SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = null
    ) {
        val imageComparer = createImageComparer(fonts)

        assertPlot(
            imageComparer = imageComparer,
            expectedFileName = expectedFileName,
            plotSpec = plotSpec,
            width = width,
            height = height,
            unit = unit,
            dpi = dpi,
            scale = scale
        )
    }

    @BeforeTest
    fun setUp() {
        fonts.clear()
        fonts.add("fonts/NotoSans-Regular.ttf")
        fonts.add("fonts/NotoSans-Bold.ttf")
        fonts.add("fonts/NotoSans-Italic.ttf")
        fonts.add("fonts/NotoSans-BoldItalic.ttf")
        fonts.add("fonts/NotoSerif-Regular.ttf")
    }
}