/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param angle  The angle of the arrow head in radians (smaller numbers produce narrower, pointier arrows).
 * Essentially describes the width of the arrow head.
 * @param length The length of the arrow head (px).
 */
class ArrowSpec(val angle: Double, val length: Double, val end: End, val type: Type) {

    val isOnFirstEnd: Boolean
        get() = end == End.FIRST || end == End.BOTH

    val isOnLastEnd: Boolean
        get() = end == End.LAST || end == End.BOTH

    enum class End {
        LAST, FIRST, BOTH
    }

    enum class Type {
        OPEN, CLOSED
    }

    companion object {

        fun createArrow(
            p: DataPointAesthetics,
            start: DoubleVector,
            end: DoubleVector,
            arrowSpec: ArrowSpec
        ): SvgPathElement? {

            val abscissa = end.x - start.x
            val ordinate = end.y - start.y
            if (abscissa == 0.0 && ordinate == 0.0) return null

            // Compute the angle that the vector defined by this segment makes with the
            // X-axis (radians)
            val polarAngle = atan2(ordinate, abscissa)

            val arrowAes = arrowSpec.toArrowAes(p)

            val arrow = createElement(polarAngle, end.x, end.y, arrowSpec)
            GeomHelper.decorate(arrow, arrowAes, applyAlphaToAll = true)

            return arrow
        }

        /**
         * @param polarAngle Angle between X-axis and the arrowed vector.
         */
        private fun createElement(polarAngle: Double, x: Double, y: Double, arrowSpec: ArrowSpec): SvgPathElement {
            val xs = with(arrowSpec) { doubleArrayOf(x - length * cos(polarAngle - angle), x, x - length * cos(polarAngle + angle)) }
            val ys = with(arrowSpec) { doubleArrayOf(y - length * sin(polarAngle - angle), y, y - length * sin(polarAngle + angle)) }

            val b = SvgPathDataBuilder(true)
                .moveTo(xs[0], ys[0])

            for (i in 1..2) {
                b.lineTo(xs[i], ys[i], true)
            }

            if (arrowSpec.type == Type.CLOSED) {
                b.closePath()
            }

            return SvgPathElement(b.build())
        }

        private fun ArrowSpec.toArrowAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {
                private val filled = (type == Type.CLOSED)

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.FILL -> if (filled) super.get(Aes.COLOR) else Color.TRANSPARENT
                        Aes.LINETYPE -> if (filled) {
                            NamedLineType.SOLID // avoid ugly patterns if linetype is other than 'solid'
                        } else {
                            super.get(aes)
                        }
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }
    }
}
