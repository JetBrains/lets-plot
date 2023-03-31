/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point

import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesScaling
import kotlin.math.sqrt


// redundant `final` in overridden members are necessary due to kotlin-native issue:
// `Not in vtable error` #2865
// https://github.com/JetBrains/kotlin-native/issues/2865
enum class NamedShape(
    @Suppress("RedundantModalityModifier")
    final override val code: Int,
    val isSolid: Boolean = false,
    val isFilled: Boolean = false,
    private val isSmall: Boolean = false
) : PointShape {

    STICK_SQUARE(0),
    STICK_CIRCLE(1),
    STICK_TRIANGLE_UP(2),
    STICK_PLUS(3),
    STICK_CROSS(4),
    STICK_DIAMOND(5),
    STICK_TRIANGLE_DOWN(6),
    STICK_SQUARE_CROSS(7),
    STICK_STAR(8),
    STICK_DIAMOND_PLUS(9),
    STICK_CIRCLE_PLUS(10),
    STICK_TRIANGLE_UP_DOWN(11),
    STICK_SQUARE_PLUS(12),
    STICK_CIRCLE_CROSS(13),
    STICK_SQUARE_TRIANGLE_UP(14),

    SOLID_SQUARE(15, true, false),
    SOLID_CIRCLE(16, true, false),
    SOLID_TRIANGLE_UP(17, true, false),
    SOLID_DIAMOND(18, true, false, true),

    SOLID_CIRCLE_2(19, true, false), // same as SOLID_CIRCLE
    BULLET(20, true, false, true), // same as SOLID_CIRCLE but smaller

    FILLED_CIRCLE(21, false, true),
    FILLED_SQUARE(22, false, true),
    FILLED_DIAMOND(23, false, true),
    FILLED_TRIANGLE_UP(24, false, true),
    FILLED_TRIANGLE_DOWN(25, false, true);

    val isHollow: Boolean
        get() = !(isFilled || isSolid)


    @Suppress("RedundantModalityModifier")
    final override fun size(dataPoint: DataPointAesthetics): Double {
        val diameter = if (isSmall)
            AesScaling.circleDiameterSmaller(dataPoint)
        else
            AesScaling.circleDiameter(dataPoint)
        val strokeCoeff = if (isSolid)
            0.0
        else {
            val shapeCoeff = when (this) {
                STICK_DIAMOND,
                STICK_DIAMOND_PLUS,
                FILLED_DIAMOND -> sqrt(2.0)
                STICK_PLUS,
                STICK_STAR,
                STICK_CROSS -> 2.0
                else -> 1.0
            }
            shapeCoeff * strokeWidth(dataPoint)
        }

        return diameter + strokeCoeff
    }

    @Suppress("RedundantModalityModifier")
    final override fun strokeWidth(dataPoint: DataPointAesthetics): Double {
        return if (isSolid)
            0.0
        else
            AesScaling.pointStrokeWidth(dataPoint)
    }
}
