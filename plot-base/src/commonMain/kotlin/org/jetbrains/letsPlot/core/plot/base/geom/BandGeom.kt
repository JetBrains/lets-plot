/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.FlippableGeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP

/*
  For this geometry 'isVertical' means that it has vertical bounds: ymin and ymax.
*/
class BandGeom(private val isVertical: Boolean) : GeomBase() {
    private val flipHelper = FlippableGeomHelper(isVertical)

    private val yMinAes = flipHelper.getEffectiveAes(Aes.YMIN)
    private val yMaxAes = flipHelper.getEffectiveAes(Aes.YMAX)

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.XMIN, Aes.XMAX).map(flipHelper::getEffectiveAes)
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val svgHelper = geomHelper.createSvgElementHelper()
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

            // tooltip
            val defaultColor = p.fill() ?: continue

            val isVerticallyOriented = when (isVertical) {
                true -> !ctx.flipped
                false -> ctx.flipped
            }

            val axisTooltip = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(0.0)
                .defaultKind(VERTICAL_TOOLTIP.takeUnless { isVerticallyOriented } ?: HORIZONTAL_TOOLTIP)
                .defaultColor(defaultColor, alpha = null)
                .defaultCoord(viewPort.xRange().lowerEnd)

            val hintsCollection = HintsCollection(p, geomHelper)
                .addHint(axisTooltip.create(yMinAes))
                .addHint(axisTooltip.create(yMaxAes))

            val tooltipParams = GeomTargetCollector.TooltipParams(
                tipLayoutHints = hintsCollection.hints,
                markerColors = HintColorUtil.createColorMarkerMapper(GeomKind.BAND, ctx)(p)
            )

            geomHelper.toClient(rect.flipIf(!isVertical), p)?.let { r ->
                ctx.targetCollector.addPolygon(r.points, p.index(), tooltipParams)
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}