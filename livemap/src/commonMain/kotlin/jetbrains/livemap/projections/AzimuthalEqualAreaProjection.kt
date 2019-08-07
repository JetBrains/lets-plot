package jetbrains.livemap.projections

import kotlin.math.asin
import kotlin.math.sqrt

internal class AzimuthalEqualAreaProjection : AzimuthalBaseProjection() {
    override fun scale(cxcy: Double): Double = sqrt(2.0 / (1.0 + cxcy))

    override fun angle(z: Double): Double = 2.0 * asin(z / 2.0)
}