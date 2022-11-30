/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.builder.sampling.Samplings.pick
import jetbrains.datalore.plot.builder.sampling.Samplings.random
import jetbrains.datalore.plot.builder.sampling.Samplings.systematic
import jetbrains.datalore.plot.builder.sampling.Samplings.systematicGroup
import jetbrains.datalore.plot.builder.sampling.Samplings.vertexDp

/*
 None:
      livemap
      raster
      image
 */
object DefaultSampling {
    private const val SEED = 37L

    val SAFETY_SAMPLING = random(200000, SEED)

    // point-like
    val POINT = random(50000, SEED)   // optimized
    val TILE = random(50000, SEED)    // optimized
    val BIN_2D = TILE
    val AB_LINE = random(5000, SEED)
    val H_LINE = random(5000, SEED)
    val V_LINE = random(5000, SEED)
    val JITTER = random(5000, SEED)
    val Q_Q = random(5000, SEED)
    val Q_Q_LINE = random(5000, SEED)
    val RECT = random(5000, SEED)
    val SEGMENT = random(5000, SEED)
    val TEXT = random(500, SEED)

    // range
    val ERROR_BAR = random(500, SEED)
    val CROSS_BAR = random(500, SEED)
    // val BOX_PLOT = random(500, SEED) - tmp disabled (see GeomProto)
    val LINE_RANGE = random(500, SEED)
    val POINT_RANGE = random(500, SEED)

    // bars
    val BAR = pick(50)
    val HISTOGRAM = systematic(500)
    val DOT_PLOT = systematic(500)
    val Y_DOT_PLOT = systematic(500)
    val PIE = systematic(500)

    // lines
    val LINE = systematic(5000)
    val RIBBON = systematic(5000)
    val AREA = systematic(5000)
    val DENSITY = systematic(5000)
    val AREA_RIDGES = systematic(5000)
    val VIOLIN = pick(50)
    val FREQPOLY = systematic(5000)
    val STEP = systematic(5000)

    // polygons
    val PATH = vertexDp(20000)
    val POLYGON = vertexDp(20000)
    val MAP = vertexDp(20000)

    // groups
    val SMOOTH = systematicGroup(200)
    val CONTOUR = systematicGroup(200)
    val CONTOURF = systematicGroup(200)
    val DENSITY2D = systematicGroup(200)
    val DENSITY2DF = systematicGroup(200)
}