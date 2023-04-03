/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.render.SvgRoot
import kotlin.math.pow

class LollipopGeom : PointGeom() {
    var slope: Double = DEF_SLOPE
    var intercept: Double = DEF_INTERCEPT

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val segmentAesthetics = calculateSegmentAesthetics(aesthetics)
        SegmentGeom().build(root, segmentAesthetics, pos, coord, ctx)

        super.buildIntern(root, aesthetics, pos, coord, ctx)
    }

    private fun calculateSegmentAesthetics(
        aesthetics: Aesthetics
    ): Aesthetics {
        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y)

        val xValues = dataPoints.map { it.x()!! }
        val yValues = dataPoints.map { it.y()!! }
        val projXValues = dataPoints.map { p ->
            (p.x()!! + slope * (p.y()!! - intercept)) / (1 + slope.pow(2))
        }
        val projYValues = projXValues.map { slope * it + intercept }

        val lineTypes = dataPoints.map { it.lineType() }
        val colors = dataPoints.map { it.color() }
        val alphas = dataPoints.map { it.alpha() }

        return AestheticsBuilder(xValues.size)
            .x(AestheticsBuilder.list(xValues))
            .y(AestheticsBuilder.list(yValues))
            .aes(Aes.XEND, AestheticsBuilder.list(projXValues))
            .aes(Aes.YEND, AestheticsBuilder.list(projYValues))
            .lineType(AestheticsBuilder.list(lineTypes))
            .color(AestheticsBuilder.list(colors))
            .alpha(AestheticsBuilder.list(alphas))
            .build()
    }

    companion object {
        const val DEF_SLOPE = 0.0
        const val DEF_INTERCEPT = 0.0

        const val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}