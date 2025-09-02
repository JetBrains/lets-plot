package org.jetbrains.letsPlot.raster.shape

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.DoubleComparator
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.raster.mapping.svg.SvgTransformParser
import kotlin.test.Test

class SvgTransformParserTest {

    @Test
    fun simple() {
        val t = SvgTransformParser.parseSvgTransform("rotate(90.0 524.9668761079132 177.0)").single()

        assertThat(t)
            .usingRecursiveComparison()
            .withComparatorForType(DoubleComparator(1e-3), Double::class.javaObjectType)
            .isEqualTo(
                AffineTransform.makeTransform(
                    sx = 0.0, sy = 0.0,
                    rx = -1.0, ry = 1.0,
                    tx = 701.966, ty = -347.966
                )
            )
    }
}
