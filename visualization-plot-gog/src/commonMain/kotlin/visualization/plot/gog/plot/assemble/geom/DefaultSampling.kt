package jetbrains.datalore.visualization.plot.gog.plot.assemble.geom

import jetbrains.datalore.visualization.plot.gog.core.data.sampling.Samplings.pick
import jetbrains.datalore.visualization.plot.gog.core.data.sampling.Samplings.random
import jetbrains.datalore.visualization.plot.gog.core.data.sampling.Samplings.systematic
import jetbrains.datalore.visualization.plot.gog.core.data.sampling.Samplings.systematicGroup
import jetbrains.datalore.visualization.plot.gog.core.data.sampling.Samplings.vertexDp

object DefaultSampling {
    val SAFETY_SAMPLING = random(200000, 1L)

    // point-like
    internal val POINT = random(50000, null)   // optimized
    internal val TILE = random(50000, null)    // optimized
    internal val AB_LINE = random(5000, null)
    internal val H_LINE = random(5000, null)
    internal val V_LINE = random(5000, null)
    internal val JITTER = random(5000, null)
    internal val RECT = random(5000, null)
    internal val SEGMENT = random(5000, null)
    internal val TEXT = random(500, null)

    // bars
    internal val BAR = pick(50)
    internal val HISTOGRAM = systematic(500)
    internal val ERROR_BAR = pick(50)
    internal val BOX_PLOT = pick(50)

    // lines
    internal val LINE = systematic(5000)
    internal val RIBBON = systematic(5000)
    internal val AREA = systematic(5000)
    internal val DENSITY = systematic(5000)
    internal val FREQPOLY = systematic(5000)
    internal val STEP = systematic(5000)

    // polygons
    internal val PATH = vertexDp(20000)
    internal val POLYGON = vertexDp(20000)
    internal val MAP = vertexDp(20000)

    // groups
    internal val SMOOTH = systematicGroup(200)
    internal val CONTOUR = systematicGroup(200)
    internal val CONTOURF = systematicGroup(200)
    internal val DENSITY2D = systematicGroup(200)
    internal val DENSITY2DF = systematicGroup(200)
}// None:
// livemap
// raster
// image
