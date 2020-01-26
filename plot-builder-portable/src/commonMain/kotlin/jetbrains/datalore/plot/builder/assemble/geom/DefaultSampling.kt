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
    val SAFETY_SAMPLING = random(200000, 1L)

    // point-like
    val POINT = random(50000, null)   // optimized
    val TILE = random(50000, null)    // optimized
    val AB_LINE = random(5000, null)
    val H_LINE = random(5000, null)
    val V_LINE = random(5000, null)
    val LINE_RANGE = random(5000, null)
    val JITTER = random(5000, null)
    val RECT = random(5000, null)
    val SEGMENT = random(5000, null)
    val TEXT = random(500, null)

    // bars
    val BAR = pick(50)
    val HISTOGRAM = systematic(500)
    val ERROR_BAR = pick(50)
    val CROSS_BAR = pick(50)
    val BOX_PLOT = pick(50)

    // lines
    val LINE = systematic(5000)
    val RIBBON = systematic(5000)
    val AREA = systematic(5000)
    val DENSITY = systematic(5000)
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