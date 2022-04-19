/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.linetype.NamedLineType
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgPathElement
import kotlin.math.cos
import kotlin.math.sin

class ArrowSpec
/**
 * @param angle  The angle of the arrow head in radians (smaller numbers produce narrower, pointier arrows).
 * Essentially describes the width of the arrow head.
 * @param length The length of the arrow head (px).
 */
(val angle: Double, val length: Double, val end: End, val type: Type) {

    val isOnFirstEnd: Boolean
        get() = end == End.FIRST || end == End.BOTH

    val isOnLastEnd: Boolean
        get() = end == End.LAST || end == End.BOTH

    /**
     * @param polarAngle Angle between X-axis and the arrowed vector.
     */
    fun createElement(polarAngle: Double, x: Double, y: Double): SvgPathElement {
        val xs = doubleArrayOf(x - length * cos(polarAngle - angle), x, x - length * cos(polarAngle + angle))
        val ys = doubleArrayOf(y - length * sin(polarAngle - angle), y, y - length * sin(polarAngle + angle))

        val b = SvgPathDataBuilder(true)
                .moveTo(xs[0], ys[0])

        for (i in 1..2) {
            b.lineTo(xs[i], ys[i], true)
        }

        if (type == Type.CLOSED) {
            b.closePath()
        }

        return SvgPathElement(b.build())
    }

    fun toArrowAes(p: DataPointAesthetics): DataPointAesthetics {
        return object : DataPointAestheticsDelegate(p) {
            private val myFilled = type == Type.CLOSED

            override fun fill(): Color? {
                return if (myFilled) {
                    color()
                } else Color.TRANSPARENT
            }

            override fun lineType(): LineType {
                return if (myFilled) {
                    NamedLineType.SOLID // avoid ugly patterns if linetype is other than 'solid'
                } else super.lineType()
            }
        }
    }

    enum class End {
        LAST, FIRST, BOTH
    }

    enum class Type {
        OPEN, CLOSED
    }
}
