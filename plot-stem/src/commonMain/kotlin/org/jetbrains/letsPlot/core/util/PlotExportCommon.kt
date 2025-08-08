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
            fun fromName(name: String): SizeUnit {
                return SizeUnit.entries.find { it.value.equals(name, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Unknown size unit: $name")
            }
        }
    }

    // Estimates the size of the image based on the plot size, size unit, and DPI.
    // Returns a Triple containing:
    // 1. SizingPolicy: The policy for sizing the image.
    // 2. Double: The scale factor for rendering the image.
    fun estimateExportConfig(plotSize: DoubleVector?, dpi: Number?, unit: SizeUnit? = null, scaleFactor: Number? = null): Pair<SizingPolicy, Double> {
        val actualScale: Double
        val actualDpi: Double?
        val actualUnit: SizeUnit
        if (plotSize == null && dpi == null && unit == null) {
            // Simple saving of a plot with its default size.
            // Auto-scale to 2.0x for better quality
            actualScale = scaleFactor?.toDouble() ?: 2.0
            actualDpi = null // prevent any DPI scaling
            actualUnit = SizeUnit.PX // unit is not specified, so default to pixels
        } else if (plotSize != null && unit == SizeUnit.PX && dpi == null){
            // Plot size is specified in pixels, dpi is not specified
            actualDpi = null // prevent any DPI scaling - output image will have the same size in pixels as plotSize
            actualScale = scaleFactor?.toDouble() ?: 1.0
            actualUnit = SizeUnit.PX
        } else {
            // Regular case with plot size in physical units or DPI specified
            actualScale = scaleFactor?.toDouble() ?: 1.0 // Default is 1.0 to preserve the specified size
            actualDpi = dpi?.toDouble() ?: 300.0 // Default DPI is 300 if not specified - for printing
            actualUnit = unit ?: SizeUnit.IN // Default size unit is inches
        }

        val sizingPolicy = when {
            plotSize == null -> SizingPolicy.keepFigureDefaultSize()
            else -> {
                // Build the plot in logical pixels (always 96 DPI) and then render it scaled.
                // Otherwise, the plot will be rendered incorrectly, i.e., with too many tick labels and small font sizes.
                val (logicalWidth, logicalHeight) = when (actualUnit) {
                    SizeUnit.IN -> plotSize.mul(96.0) //(w * 96) to (h * 96)
                    SizeUnit.CM -> plotSize.mul(96 / 2.54) //(w * 96 / 2.54) to (h * 96 / 2.54)
                    SizeUnit.MM -> plotSize.mul(96 / 25.4) //(w * 96 / 25.4) to (h * 96 / 25.4)
                    SizeUnit.PX -> plotSize //w to h
                }

                SizingPolicy.fixed(width = logicalWidth, height = logicalHeight)
            }
        }

        val scaleFactor = when {
            // plot with ggsize() or default size, user want to print it with a specific DPI
            actualDpi != null && plotSize == null -> actualDpi / 96.0 * actualScale
            // user want to print a plot with a specific DPI and size in physical units
            actualDpi != null && plotSize != null && actualUnit.isPhysicalUnit -> actualDpi.toDouble() / 96.0 * actualScale
            else -> actualScale // no additional scaling needed
        }

        println("estimateExportConfig() - plotSize: $plotSize, dpi: $dpi, unit: $unit, scaleFactor: $scaleFactor")
        return Pair(sizingPolicy, scaleFactor)
    }

}