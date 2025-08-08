package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

object PlotExportCommon {
    enum class SizeUnit(val value: String, val isPhysicalUnit: Boolean) {
        PX("px", false),
        MM("mm", true),
        CM("cm", true),
        IN("in", true);

        override fun toString(): String {
            return value
        }

        companion object {
            fun fromName(name: String): SizeUnit? {
                return SizeUnit.entries.find { it.value.equals(name, ignoreCase = true) }
            }
        }
    }

    // Estimates the size of the image based on the plot size, size unit, and DPI.
    // Returns a Triple containing:
    // 1. SizingPolicy: The policy for sizing the image.
    // 2. Double: The scale factor for rendering the image.
    fun computeExportParameters(
        plotSize: DoubleVector? = null,
        dpi: Number? = null,
        unit: SizeUnit? = null,
        scaleFactor: Number? = null
    ): Triple<SizingPolicy, Double, SizeUnit> {
        val exportScale: Double
        val exportUnit: SizeUnit

        if (plotSize == null && dpi == null && unit == null) {
            // ggsave(png) - user wants to save a plot without specifying size, DPI, or unit
            // Size from ggsize() or default, meaning the unit is assumed to be pixels
            exportUnit = SizeUnit.PX
            exportScale = scaleFactor?.toDouble() ?: 2.0 // The default scale factor is 2.0 for better quality
        } else if (plotSize == null && unit == null && dpi != null) {
            // ggsave(png, dpi=150) - user wants to scale the output based on DPI with no specified size and unit
            // This means pixel ggsize() or default size is used to determine the size of the plot
            exportUnit = SizeUnit.PX
            exportScale = scaleFactor?.toDouble() ?: 1.0
        } else if (plotSize != null && unit == null) {
            // ggsave(png, 3, 2) - user wants to save a plot with a specific size without specifying unit
            // Size is assumed to be in inches
            exportUnit = SizeUnit.IN
            exportScale = scaleFactor?.toDouble() ?: 1.0 // Default scaling to preserve the specified size
        } else if (plotSize != null && unit != null) {
            // ggsave(png, w=3, h=2, unit=cm, dpi=150)
            // user wants to save a plot with a specific size and unit
            exportUnit = unit
            exportScale = scaleFactor?.toDouble() ?: 1.0 // Default scaling to preserve the specified size
        } else {
            // ggsave(png, w=3, h=2, unit=cm)
            // ggsave(png, w=3, h=2, unit=cm, scale=2)
            exportUnit = unit ?: SizeUnit.PX // Default size unit is inches
            exportScale = scaleFactor?.toDouble() ?: 1.0 // Default scaling to preserve the specified size
        }

        val exportDpi = dpi?.toDouble() ?: when (exportUnit) {
            SizeUnit.IN,
            SizeUnit.CM,
            SizeUnit.MM -> 300.0

            SizeUnit.PX -> 96.0
        }


        val sizingPolicy = when {
            plotSize == null -> SizingPolicy.keepFigureDefaultSize()
            else -> {
                // Build the plot in logical pixels (always 96 DPI) and then render it scaled.
                // Otherwise, the plot will be rendered incorrectly, i.e., with too many tick labels and small font sizes.
                val (logicalWidth, logicalHeight) = when (exportUnit) {
                    SizeUnit.IN -> plotSize.mul(96.0) //(w * 96) to (h * 96)
                    SizeUnit.CM -> plotSize.mul(96 / 2.54) //(w * 96 / 2.54) to (h * 96 / 2.54)
                    SizeUnit.MM -> plotSize.mul(96 / 25.4) //(w * 96 / 25.4) to (h * 96 / 25.4)
                    SizeUnit.PX -> plotSize //w to h
                }

                SizingPolicy.fixed(width = logicalWidth, height = logicalHeight)
            }
        }

        val finalScaleFactor = exportDpi / 96.0 * exportScale

        return Triple(sizingPolicy, finalScaleFactor, exportUnit)
    }

}