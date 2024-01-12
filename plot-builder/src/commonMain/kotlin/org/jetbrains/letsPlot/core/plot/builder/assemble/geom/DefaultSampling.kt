/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.geom

import org.jetbrains.letsPlot.core.plot.builder.sampling.Samplings.random
import org.jetbrains.letsPlot.core.plot.builder.sampling.Samplings.systematic
import org.jetbrains.letsPlot.core.plot.builder.sampling.Samplings.systematicGroup
import org.jetbrains.letsPlot.core.plot.builder.sampling.Samplings.vertexDp

/*
 None:
      livemap
      raster
      image
 */
object DefaultSampling {
    private const val SEED = 37L

    val SAFETY_SAMPLING = random(200_000, SEED)

    // basis
    val POINT = random(100_000, SEED)   // optimized
    val LINE = systematic(50_000)
    val PATH = vertexDp(50_000)  // ToDo: vertex sampling has issues.
    val SEGMENT = random(10_000, SEED)
    val SPOKE = random(10_000, SEED)

    val RECT = random(20_000, SEED)
    val TEXT = random(5_000, SEED)

    val BAR = systematic(5_000)
    private val COMPLEX_STAT = systematic(50_000)

    val CONTOUR = systematicGroup(5_000)


    // points
    val TILE = POINT    // optimized
    val BIN_2D = POINT
    val JITTER = POINT
    val Q_Q = POINT
    val PIE = POINT

    // lines
    val Q_Q_LINE = LINE
    val RIBBON = LINE
    val AREA = LINE
    val DENSITY = LINE
    val FREQPOLY = LINE
    val STEP = LINE
    val SMOOTH = LINE

    // segments
    val AB_LINE = SEGMENT
    val H_LINE = SEGMENT
    val V_LINE = SEGMENT

    // paths
    val POLYGON = PATH
    val MAP = PATH

    // bars
    val ERROR_BAR = BAR
    val CROSS_BAR = BAR

    // val BOX_PLOT = random(500, SEED) - tmp disabled (see GeomProto)
    val LINE_RANGE = BAR
    val POINT_RANGE = BAR
    val LOLLIPOP = BAR

    val HISTOGRAM = BAR
    val DOT_PLOT = BAR

    // complex
    val AREA_RIDGES = COMPLEX_STAT
    val VIOLIN = COMPLEX_STAT
    val Y_DOT_PLOT = COMPLEX_STAT

    // contours
    val CONTOURF = CONTOUR
    val DENSITY2D = CONTOUR
    val DENSITY2DF = CONTOUR
}