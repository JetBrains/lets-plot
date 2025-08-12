package org.jetbrains.letsPlot.core.util

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.RecursiveComparisonAssert
import org.assertj.core.util.DoubleComparator
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit.CM
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit.IN
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit.PX
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import kotlin.test.Test

class PlotExportCommonTest {
    @Test
    fun `ggsave(p) should scale output to 2x for better quality`() {
        PlotExportCommon.computeExportParameters().let { (sizingPolicy, scale, unit) ->
            assertThat(sizingPolicy).isEqualTo(SizingPolicy.keepFigureDefaultSize())
            assertThat(scale).isEqualTo(2.0) // Default scale factor is 2.0
            assertThat(unit).isEqualTo(PX)
        }
    }

    @Test
    fun `ggsave(p, scale=1) should not scale`() {
        PlotExportCommon.computeExportParameters(scaleFactor = 1).let { (sizingPolicy, scale, unit) ->
            assertThat(sizingPolicy).isEqualTo(SizingPolicy.keepFigureDefaultSize())
            assertThat(scale).isEqualTo(1.0)
            assertThat(unit).isEqualTo(PX)
        }
    }

    @Test
    fun `ggsave(p, dpi=150) should scale output to 1_5625x`() {
        PlotExportCommon.computeExportParameters(dpi = 150).let { (sizingPolicy, scale, unit) ->
            assertThat(sizingPolicy).isEqualTo(SizingPolicy.keepFigureDefaultSize())
            assertThat(scale).isEqualTo(150.0 / 96.0) // 1.5625
            assertThat(unit).isEqualTo(PX)
        }
    }

    @Test
    fun `ggplot(p, w=5, h=3) should output 480x288 pixels with 3_125x scale`() {
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(5, 3))
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(5 * 96.0, 3 * 96.0))
                assertThat(scale).isEqualTo(300.0 / 96.0) // = 3.125
                assertThat(unit).isEqualTo(IN)
            }
    }

    @Test
    fun `ggplot(p, w=5, h=3, dpi=150) should output 480x288 pixels with 1_5625x scale`() {
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(5, 3), dpi = 150)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(5 * 96.0, 3 * 96.0))
                assertThat(scale).isEqualTo(150.0 / 96.0) // = 1.5625
                assertThat(unit).isEqualTo(IN)
            }
    }

    @Test
    fun `ggsave(p, w=300, h=200, unit=px) should output exactly 300x200 pixels`() {
        // Output should have exactly the specified size in pixels
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(300, 200), unit = PX)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(300.0, 200.0))
                assertThat(scale).isEqualTo(1.0) // No scaling for pixel unit
                assertThat(unit).isEqualTo(PX)
            }
    }

    @Test
    fun `ggsave(p, w=300, h=200, unit=px, scale=2) should output 300x200 pixels with scale 2x`() {
        // Output should have exactly the specified size in pixels
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(300, 200), unit = PX, scaleFactor = 2)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(300.0, 200.0))
                assertThat(scale).isEqualTo(2.0) // Scaling factor is applied
                assertThat(unit).isEqualTo(PX)
            }
    }

    @Test
    fun `ggsave(p, w=300, h=200, unit=px, dpi=150) should output 300x200 pixels with 1_5625x scale`() {
        // Output should have exactly the specified size in pixels
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(300, 200), unit = PX, dpi = 150)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(300.0, 200.0))
                assertThat(scale).isEqualTo(150.0 / 96.0) // = 1.5625
                assertThat(unit).isEqualTo(PX)
            }
    }

    @Test
    fun `ggsave(p, w=300, h=200, unit=px, dpi=150, scale=2) should output 300x200 pixels with 1_5625x scale`() {
        // Output should have exactly the specified size in pixels
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(300, 200), unit = PX, dpi = 150, scaleFactor = 2)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(300.0, 200.0))
                assertThat(scale).isEqualTo(150.0 / 96.0 * 2.0) // = 3.125
                assertThat(unit).isEqualTo(PX)
            }
    }

    @Test
    fun `ggsave(p, w=5, h=3, unit=cm) should output 189x113 pixels with 3_125x scale`() {
        // Output should have the specified size in centimeters at 300 DPI
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(5, 3), unit = CM)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(5.0 * 96.0 / 2.54, 3.0 * 96.0 / 2.54)) // Convert cm to pixels
                assertThat(scale).isEqualTo(300.0 / 96.0) // = 3.125
                assertThat(unit).isEqualTo(CM)
            }
    }

    @Test
    fun `ggsave(p, w=5, h=3, unit=cm, dpi=150) should output 189x113 pixels with 1_5625x scale`() {
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(5, 3), unit = CM, dpi = 150)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(5 * 96.0 / 2.54, 3 * 96.0 / 2.54)) // Convert cm to pixels
                assertThat(scale).isEqualTo(150.0 / 96.0) // = 1.5625
                assertThat(unit).isEqualTo(CM)
            }
    }

    @Test
    fun `ggsave(p, w=5, h=3, unit=cm, scale=2) should output 189x113 pixels with 6_25x scale`() {
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(5, 3), unit = CM, scaleFactor = 2)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(5 * 96 / 2.54, 3 * 96 / 2.54)) // Convert cm to pixels
                assertThat(scale).isEqualTo(300.0 / 96.0 * 2.0) // = 6.25
                assertThat(unit).isEqualTo(CM)
            }
    }

    @Test
    fun `ggsave(p, w=5, h=3, unit=cm, dpi=150, scale=2) should output 189x113 pixels with 1_5625x scale`() {
        PlotExportCommon.computeExportParameters(plotSize = DoubleVector(5.0, 3.0), unit = CM, dpi=150, scaleFactor = 2)
            .let { (sizingPolicy, scale, unit) ->
                assertThat(sizingPolicy).isEqualTo(SizingPolicy.fixed(5.0 * 96.0 / 2.54, 3.0 * 96.0 / 2.54)) // Convert cm to pixels
                assertThat(scale).isEqualTo(150.0 / 96.0 * 2.0) // = 3.125
                assertThat(unit).isEqualTo(CM)
            }
    }


    private fun assertThat(actual: SizingPolicy): RecursiveComparisonAssert<*> {
        return Assertions.assertThat(actual).usingRecursiveComparison()
            .withComparatorForType(DoubleComparator(0.1), Double::class.javaObjectType)
    }
}
