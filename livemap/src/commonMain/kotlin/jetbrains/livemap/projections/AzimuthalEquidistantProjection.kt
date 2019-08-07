package jetbrains.livemap.projections

import kotlin.math.acos
import kotlin.math.sin

internal class AzimuthalEquidistantProjection : AzimuthalBaseProjection() {
    override fun scale(cxcy: Double): Double = acos(cxcy).let { if (it == 0.0) 0.0 else it / sin(it) }

    override fun angle(z: Double): Double = z
}