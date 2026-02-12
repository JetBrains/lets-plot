package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.livemap.Client

internal class SymbolizerContext(
    val tileSize: Vec<Client>
) {
    fun pinLabel(bbox: DoubleRectangle) {
        myLabelBounds.add(bbox)
    }

    fun intersectsAnyLabel(bbox: DoubleRectangle): Boolean {
        for (labelBounds in myLabelBounds) {
            if (labelBounds.intersects(bbox)) {
                return true
            }
        }
        return false
    }

    fun isInsideTile(label: String, font: Font, p: Vec<Client>, ctx: Context2d): Boolean {
        val f = org.jetbrains.letsPlot.commons.values.Font(
            family = FontFamily(font.fontFamily, monospaced = false),
            size = font.fontSize.toInt(),
            isBold = font.fontWeight == FontWeight.BOLD,
            isItalic = font.fontStyle == FontStyle.ITALIC
        )

        val roughWidth = TextWidthEstimator.widthCalculator(label, f)
        val roughBbox = DoubleRectangle.XYWH(
            x = p.x - roughWidth / 2,
            y = p.y - font.fontSize / 2,
            width = roughWidth,
            height = font.fontSize
        ).inflate(20.0) // because of possible inaccuracy of TextWidthEstimator

        return !(roughBbox.right < 0 || roughBbox.left > tileSize.x || roughBbox.bottom < 0 || roughBbox.top > tileSize.y)
    }

    fun measureTextWidth(text: String, font: Font, ctx: Context2d): Double {
        val cacheKey = "${font.fontStyle}-${font.fontWeight}-${font.fontSize}-${font.fontFamily}-$text"
        return measureTextWidthCache.getOrPut(cacheKey) {
            ctx.setFont(font)
            ctx.measureTextWidth(text)
        }
    }

    private val myLabelBounds = mutableListOf<DoubleRectangle>()
    private val measureTextWidthCache = mutableMapOf<String, Double>()
}