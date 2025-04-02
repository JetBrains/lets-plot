/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP

/*
  For this geometry 'isVertical' means that it has vertical bounds: ymin and ymax.

  Instead of using this parameter, 'band' could be added to the list of orientable geoms in the LayerConfig::isOrientationApplicable(),
  but it's hard to get the correct behavior with polar coordinates that way.
*/
class BandGeom(private val isVertical: Boolean) : GeomBase() {
    private val yMinAes = if (isVertical) Aes.YMIN else Aes.XMIN
    private val yMaxAes = if (isVertical) Aes.YMAX else Aes.XMAX

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val svgHelper = GeomHelper(pos, coord, ctx).createSvgElementHelper()
            .setStrokeAlphaEnabled(true)
            .setResamplingEnabled(!coord.isLinear)

        val viewPort = overallAesBounds(ctx).flipIf(!isVertical)

        for (p in aesthetics.dataPoints()) {
            val yMin = p.finiteOrNull(yMinAes) ?: continue
            val yMax = p.finiteOrNull(yMaxAes) ?: continue
            if (yMin > yMax) continue
            val rect = DoubleRectangle.hvRange(viewPort.xRange(), DoubleSpan(yMin, yMax))
            val (topSide, _, _, bottomSide) = rect.parts.toList()

            // strokeScaler = { 0.0 } to avoid rendering stroke
            val (rectSvg, _) = svgHelper.createRectangle(rect.flipIf(!isVertical), p, strokeScaler = { 0.0 }) ?: continue
            val (topSvg, _) = svgHelper.createLine(topSide.flipIf(!isVertical), p) ?: continue
            val (bottomSvg, _) = svgHelper.createLine(bottomSide.flipIf(!isVertical), p) ?: continue

            root.add(rectSvg)
            root.add(topSvg)
            root.add(bottomSvg)
        }

        buildHints(aesthetics, pos, coord, ctx, viewPort)
    }

    private fun buildHints(
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        viewPort: DoubleRectangle
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorMapper = HintColorUtil.createColorMarkerMapper(GeomKind.BAND, ctx)
        val isVerticallyOriented = when (isVertical) {
            true -> !ctx.flipped
            false -> ctx.flipped
        }
        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(0.0)
            .defaultKind(VERTICAL_TOOLTIP.takeIf { isVerticallyOriented } ?: HORIZONTAL_TOOLTIP)

        for (p in aesthetics.dataPoints()) {
            for (x in resample(viewPort.xRange())) {
                for (aes in listOf(yMinAes, yMaxAes)) {
                    val value = p[aes] ?: continue
                    val defaultColor = p.fill() ?: continue

                    hint.defaultCoord(x)
                        .defaultColor(defaultColor, alpha = null)

                    val hintsCollection = HintsCollection(p, helper)
                        .addHint(hint.create(aes))

                    val tooltipParams = GeomTargetCollector.TooltipParams(
                        tipLayoutHints = hintsCollection.hints,
                        markerColors = colorMapper(p)
                    )
                    helper.toClient(DoubleVector(x, value).flipIf(!isVertical), p)?.let { point ->
                        ctx.targetCollector.addPoint(p.index(), point, 0.0, tooltipParams)
                    }
                }
            }
        }
    }

    private fun resample(range: DoubleSpan): List<Double> {
        return (0 until TOOLTIP_SAMPLE_SIZE).map { i ->
            range.lowerEnd + (i.toDouble() / (TOOLTIP_SAMPLE_SIZE - 1)) * (range.upperEnd - range.lowerEnd)
        }
    }

    companion object {
        const val TOOLTIP_SAMPLE_SIZE = 512
        const val HANDLES_GROUPS = false
    }
}