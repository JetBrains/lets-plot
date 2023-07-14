/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

open class HitShape private constructor(val kind: Kind, private val shape: Any) {

    val point: DoubleCircle
        get() = shape as DoubleCircle

    val rect: DoubleRectangle
        get() = shape as DoubleRectangle

    open val points: List<DoubleVector>
        get() = throw IllegalStateException("Not applicable to $kind")

    enum class Kind {
        POINT, RECT, POLYGON, PATH
    }

    class DoubleCircle(val center: DoubleVector, val radius: Double)

    companion object {
        fun point(p: DoubleVector, radius: Double): HitShape {
            return HitShape(Kind.POINT, DoubleCircle(p, radius))
        }

        fun rect(r: DoubleRectangle): HitShape {
            return HitShape(Kind.RECT, r)
        }

        fun path(points: List<DoubleVector>): HitShape {
            return shapeWithPath(Kind.PATH, points)
        }

        fun polygon(points: List<DoubleVector>): HitShape {
            return shapeWithPath(Kind.POLYGON, points)
        }

        private fun shapeWithPath(kind: Kind, points: List<DoubleVector>): HitShape {
            return object : HitShape(kind, points) {
                override val points: List<DoubleVector>
                    get() = points
            }
        }
    }
}
