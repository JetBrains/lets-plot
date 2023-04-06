/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

open class HitShape private constructor(val kind: Kind, private val shape: Any, val hintOffset: Double) {

    val point: DoubleVector
        get() = shape as DoubleVector

    val rect: DoubleRectangle
        get() = shape as DoubleRectangle

    open val points: List<DoubleVector>
        get() = throw IllegalStateException("Not applicable to $kind")

    enum class Kind {
        POINT, RECT, POLYGON, PATH
    }

    companion object {
        fun point(p: DoubleVector, radius: Double): HitShape {
            return HitShape(Kind.POINT, p, radius)
        }

        fun rect(r: DoubleRectangle, hintOffset: Double): HitShape {
            return HitShape(Kind.RECT, r, hintOffset)
        }

        fun path(points: List<DoubleVector>): HitShape {
            return shapeWithPath(Kind.PATH, points)
        }

        fun polygon(points: List<DoubleVector>): HitShape {
            return shapeWithPath(Kind.POLYGON, points)
        }

        private fun shapeWithPath(kind: Kind, points: List<DoubleVector>): HitShape {
            return object : HitShape(kind, points, hintOffset = 0.0) {
                override val points: List<DoubleVector>
                    get() = points
            }
        }
    }
}
