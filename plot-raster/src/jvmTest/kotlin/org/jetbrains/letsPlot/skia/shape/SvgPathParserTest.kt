/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.raster.mapping.svg.SvgPathParser
import org.jetbrains.letsPlot.raster.mapping.svg.SvgTransformParser
import org.junit.Test

class SvgPathParserTest {

    @Test
    fun arc() {
        val str = SvgPathDataBuilder()
            .moveTo(232.97984468811742, 170.5)
            .lineTo(189.6785744988955, 195.5)
            .ellipticalArc(50.0, 50.0, 0.0, false, true, 232.97984468811742, 120.5)
            .lineTo(232.97984468811742, 170.5)
            .ellipticalArc(0.0, 0.0, 0.0, false, false, 232.97984468811742, 170.5)
            .build()
            .toString()

        val path = SvgPathParser.parse(str).getCommands()

        val p2 = SvgTransformParser.parsePath(str)

        assertThat(path).hasSize(5)
        assertThat(path)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Path2d.MoveTo(232.97984468811742, 170.5),
                Path2d.LineTo(189.6785744988955, 195.5),
                Path2d.CubicCurveTo(
                    start = DoubleVector(189.6785744988955, 195.5),
                    controlPoints = listOf(
                        DoubleVector(180.74693475119142, 180.02994616207485),
                        DoubleVector(180.74693475119142, 160.97005383792515),
                        DoubleVector(189.6785744988955, 145.5),
                        DoubleVector(198.61021424659958, 130.02994616207485),
                        DoubleVector(215.11656519270923, 120.5),
                        DoubleVector(232.97984468811742, 120.5)
                    ),
                ),
                Path2d.LineTo(232.97984468811742, 170.5),
                Path2d.LineTo(232.97984468811742, 170.5)
            )
    }
}