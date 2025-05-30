/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import demoAndTestShared.parsePlotSpec
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.imagick.canvas.MagickContext2d
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class PlotTest {
    private val outDir: String = getCurrentDir() + "/build/image-test/"
    private val expectedDir: String = getCurrentDir() + "/src/nativeTest/resources/expected/"
    private val w = 100.0
    private val h = 100.0

    private val strokeColor = "#000000"
    private val fillColor = "#000000"
    private val filledStrokeColor = "#000080"
    private val strokedFillColor = "#FFC000"

    init {
        mkDir(outDir)
    }

    val imageComparer = ImageComparer(
        expectedDir = expectedDir,
        outDir = outDir
    )

    fun createCanvas(
        width: Number = w,
        height: Number = h,
        pixelDensity: Double = 1.0
    ): Pair<MagickCanvas, MagickContext2d> {
        val canvas = MagickCanvas.create(width = width, height = height, pixelDensity = pixelDensity)
        val context2d = canvas.context2d as MagickContext2d
        return canvas to context2d
    }

    @Test
    fun barPlot() {
        val OUR_DATA = "   {" +
                "      'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
                "   }"
        val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'data': " + OUR_DATA +
                    "           ," +
                    "   'mapping': {" +
                    "             'x': 'time'," +
                    "             'color': 'time'," +
                    "             'fill': 'time'" +
                    "           }," +
                    "   'layers': [" +
                    "               {" +
                    "                  'geom': 'bar'," +
                    "                  'alpha': '0.5'" +
                    "               }" +
                    "           ]" +
                    "}"


        imageComparer.assertImageEquals("plot_bar_test.bmp", spec)
    }
}