/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.raster.mapping.svg.SvgPathParser
import org.junit.Test

class SvgPathParserTest {

    @Test
    fun `empty path should not fail`() {
        val pathData = ""
        val path = SvgPathParser.parse(pathData)

        assertThat(path.getCommands()).isEmpty()
    }

    @Test
    fun `arc with radius and rotation should be parsed correctly`() {
        val pathData = "M100 100 A30 50 45 0 1 200 200"
        val path = SvgPathParser.parse(pathData)

        assertThat(path)
            .hasCommands(
                Path2d.MoveTo(100.0, 100.0),
                Path2d.CubicCurveTo(
                    listOf(
                        DoubleVector(117, 53),
                        DoubleVector(153, 38),
                        DoubleVector(180, 65),
                        DoubleVector(208, 93),
                        DoubleVector(216, 153),
                        DoubleVector(200, 200)
                    )
                )
            )

    }


    @Test
    fun `arc after lineTo should not prepend with extra lineTo`() {
        val pathData =
            """M233.0 170.5 
                |L233.0 120.5 
                |A50.0 50.0 0.0 1 1 190.0 195.5 
                |L233.0 170.5 
                |A0.0 0.0 0.0 1 0 233.0 170.5
                |""".trimMargin()
        val path = SvgPathParser.parse(pathData)

        assertThat(path)
            .hasCommands(
                Path2d.MoveTo(233.0, 170.5),
                Path2d.LineTo(233.0, 120.5),
                Path2d.CubicCurveTo(
                    listOf(
                        DoubleVector(257.0, 120.0),
                        DoubleVector(278.0, 137.0),
                        DoubleVector(282.0, 161.0),
                        DoubleVector(286.0, 185.0),
                        DoubleVector(273.0, 209.0),
                        DoubleVector(250.0, 217.0),
                        DoubleVector(227.0, 225.0),
                        DoubleVector(202.0, 216.0),
                        DoubleVector(189.0, 195.0)
                    )
                ),
                Path2d.LineTo(233.0, 170.5),
                Path2d.LineTo(233.0, 170.5),

                )
    }

    @Test
    fun `arc with zero radius should be transformed to lineTo`() {
        val pathData =
            """M233.0 170.5 
                |A0.0 0.0 0.0 1 0 233.0 170.5
                |""".trimMargin()
        val path = SvgPathParser.parse(pathData)

        assertThat(path)
            .hasCommands(
                Path2d.MoveTo(233.0, 170.5),
                Path2d.LineTo(233.0, 170.5),
            )
    }

    @Test
    fun subPaths() {
        val pathData = "M0.0 0.0 h50.0 v50.0 h-50.0 zM10.0 10.0 h30.0 v30.0 h-30.0 z"

        val path = SvgPathParser.parse(pathData)

        assertThat(path)
            .hasCommands(
                Path2d.MoveTo(0.0, 0.0),
                Path2d.LineTo(50.0, 0.0),
                Path2d.LineTo(50.0, 50.0),
                Path2d.LineTo(0.0, 50.0),
                Path2d.ClosePath,
                Path2d.MoveTo(10.0, 10.0),
                Path2d.LineTo(40.0, 10.0),
                Path2d.LineTo(40.0, 40.0),
                Path2d.LineTo(10.0, 40.0),
                Path2d.ClosePath
            )
    }

    @Test
    fun `sequence of coordinates from moveTo command should produce lineToCommands`() {
        val pathData = "M0 0 50 0 50 50 0 50 z"

        val path = SvgPathParser.parse(pathData)

        assertThat(path)
            .hasCommands(
                Path2d.MoveTo(0.0, 0.0),
                Path2d.LineTo(50.0, 0.0),
                Path2d.LineTo(50.0, 50.0),
                Path2d.LineTo(0.0, 50.0),
                Path2d.ClosePath,
            )
    }
}
