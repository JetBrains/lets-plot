package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

object PlotExportCommon {
    data class ExportParameters(
        val sizingPolicy: SizingPolicy, // sizing policy to use for building the plot
        val scaleFactor: Double, // scale factor to apply to the plot rendering
        val sizeUnit: SizeUnit, // size unit of the plot
        val dpi: Double // output DPI
    )

    private const val MAX_INCHES = 20.0

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

    // Compute export parameters based on the provided plot size, DPI, unit, and scale factor.
    fun computeExportParameters(
        plotSize: DoubleVector? = null,
        dpi: Number? = null,
        unit: SizeUnit? = null,
        scaleFactor: Number? = null
    ): ExportParameters {
        @Suppress("NAME_SHADOWING")
        val dpi = dpi?.toDouble()?.takeIf(Double::isFinite)

        @Suppress("NAME_SHADOWING")
        val scaleFactor = scaleFactor?.toDouble()?.takeIf { it.isFinite() && it > 0.0 }

        val exportScale: Double
        val exportUnit: SizeUnit

        if (plotSize == null && dpi == null && unit == null) {
            // ggsave(png) - no size, no unit, no dpi
            // Use pixel ggsize (or default) and apply scale (default 2.0) for higher-resolution raster.
            exportUnit = SizeUnit.PX
            exportScale = scaleFactor ?: 2.0
        } else if (plotSize == null && unit == null && dpi != null) {
            // ggsave(png, dpi=150) - only DPI is specified.
            // Use pixel ggsize (or default), treat it as 96 PPI to get a physical size, then render at the given DPI.
            exportUnit = SizeUnit.PX
            exportScale = scaleFactor ?: 1.0
        } else if (plotSize != null && unit == null) {
            // ggsave(png, w=3, h=2) - size given without unit; assume inches.

            if (plotSize.x >= MAX_INCHES || plotSize.y >= MAX_INCHES) {
                throw IllegalArgumentException("The image size was interpreted as inches, but it seems unusually large. Please specify the size unit explicitly (px, cm, mm, in).")
            }
            exportUnit = SizeUnit.IN
            exportScale = scaleFactor ?: 1.0 // Default scaling to preserve the specified size
        } else if (plotSize != null && unit != null) {
            // ggsave(png, w=3, h=2, unit=cm, dpi=150) - size + explicit unit (dpi optional).
            // user wants to save a plot with a specific size and unit
            exportUnit = unit
            exportScale = scaleFactor ?: 1.0 // Default scaling to preserve the specified size
        } else { // plotSize == null && unit != null
            // size is not given, unit alone is ineffective
            //   ggsave(png, unit = cm)           - no width/height; unit has no effect
            //   ggsave(png, unit = cm, scale=3)  - scale applies to pixel ggsize
            exportUnit = unit ?: SizeUnit.PX
            exportScale = scaleFactor ?: 1.0
        }

        val exportDpi = dpi ?: when (exportUnit) {
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

        val finalScaleFactor = when {
            exportUnit == SizeUnit.PX && plotSize != null -> exportScale
            else -> (exportDpi / 96.0) * exportScale
        }

        return ExportParameters(sizingPolicy, finalScaleFactor, exportUnit, exportDpi)
    }

}