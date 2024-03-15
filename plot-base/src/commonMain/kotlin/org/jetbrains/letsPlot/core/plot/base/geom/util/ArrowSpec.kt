/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.math.*

/**
 * @param angle  The angle of the arrow head in radians (smaller numbers produce narrower, pointier arrows).
 * Essentially describes the width of the arrow head.
 * @param length The length of the arrow head (px).
 */
class ArrowSpec(
    val angle: Double,
    val length: Double,
    val end: End,
    val type: Type
) {

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
        fun createArrows(
            p: DataPointAesthetics,
            geometry: List<DoubleVector>,
            arrowSpec: ArrowSpec
        ): List<SvgPathElement> {
            val arrows = mutableListOf<SvgPathElement?>()
            if (arrowSpec.isOnFirstEnd) {
                val (start, end) = geometry.take(2).reversed()
                arrows += createArrowAtEnd(p, start, end, arrowSpec)
            }
            if (arrowSpec.isOnLastEnd) {
                val (start, end) = geometry.takeLast(2)
                arrows += createArrowAtEnd(p, start, end, arrowSpec)
            }
            return arrows.filterNotNull()
        }

        private fun createArrowAtEnd(
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

            val arrow = createElement(polarAngle, end, arrowSpec, listOf(start, end))
            val strokeScaler = AesScaling::strokeWidth
            GeomHelper.decorate(arrow, arrowAes, applyAlphaToAll = true, strokeScaler, filled = arrowSpec.type == Type.CLOSED)
            // Use 'stroke-miterlimit' attribute to avoid the bevelled corner
            val miterLimit = miterLength(arrowSpec.angle * 2, strokeScaler(p))
            arrow.strokeMiterLimit().set(abs(miterLimit))
            return arrow
        }

        /**
         * @param polarAngle Angle between X-axis and the arrowed vector.
         */
        private fun createElement(polarAngle: Double, tipPoint: DoubleVector, arrowSpec: ArrowSpec, geometry: List<DoubleVector>): SvgPathElement {
            val headLength = when {
                geometry.size == 2 -> min(arrowSpec.length, distance(geometry[0], geometry[1]))
                else -> arrowSpec.length
            }

            if (true) {
                val side = tipPoint.subtract(DoubleVector(headLength, 0))
                val headSide1 = side.rotateAround(tipPoint, polarAngle - arrowSpec.angle)
                val headSide2 = side.rotateAround(tipPoint, polarAngle + arrowSpec.angle)

                val b = SvgPathDataBuilder(true)
                    .moveTo(headSide1)
                    .lineTo(tipPoint)
                    .lineTo(headSide2)

                if (arrowSpec.type == Type.CLOSED) {
                    b.closePath()
                }

                return SvgPathElement(b.build())
            } else {
                val xs = with(arrowSpec) {
                    doubleArrayOf(
                        tipPoint.x - headLength * cos(polarAngle - angle),
                        tipPoint.x,
                        tipPoint.x - headLength * cos(polarAngle + angle)
                    )
                }
                val ys = with(arrowSpec) {
                    doubleArrayOf(
                        tipPoint.y - headLength * sin(polarAngle - angle),
                        tipPoint.y,
                        tipPoint.y - headLength * sin(polarAngle + angle)
                    )
                }

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
        }

        private fun ArrowSpec.toArrowAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {
                private val filled = (type == Type.CLOSED)

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.FILL -> if (filled) super.get(Aes.COLOR) else Color.TRANSPARENT
                        Aes.LINETYPE -> NamedLineType.SOLID // avoid ugly patterns if linetype is other than 'solid'
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }

        fun miterLength(headAngle: Double, strokeWidth: Double): Double {
            return strokeWidth / sin(headAngle / 2)
        }
    }
}
