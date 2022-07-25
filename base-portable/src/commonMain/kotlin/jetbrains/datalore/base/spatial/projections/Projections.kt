/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.PI

fun mercator(): Projection = MercatorProjection()
fun azimuthalEqualArea(): Projection = AzimuthalEqualAreaProjection()
fun azimuthalEqidistant(): Projection = AzimuthalEquidistantProjection()
fun conicEqualArea(y0: Double = 0.0, y1: Double = PI / 3): Projection = ConicEqualAreaProjection(y0, y1)

fun identity(): Projection = object : Projection {
    override fun project(v: DoubleVector): DoubleVector? = v

    override fun invert(v: DoubleVector): DoubleVector? = v

    override fun validRect(): DoubleRectangle {
        TODO("Not yet implemented")
    }

    override val cylindrical: Boolean
        get() = TODO("Not yet implemented")

}

internal fun finiteDoubleVectorOrNull(x: Double, y: Double): DoubleVector? = when {
    x.isFinite() && y.isFinite() -> DoubleVector(x, y)
    else -> null
}
