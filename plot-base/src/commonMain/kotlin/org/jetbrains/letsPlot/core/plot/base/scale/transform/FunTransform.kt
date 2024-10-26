/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

abstract class FunTransform(
    protected val transformFun: (Double) -> Double,
    protected val inverseFun: (Double) -> Double
) : ContinuousTransform {
    override fun apply(v: Double?): Double? {
        return if (v != null) {
            transformFun(v)
        } else {
            null
        }
    }

    override fun apply(l: List<*>): List<Double?> {
        return safeCastToDoubles(l).map { apply(it) }
    }

    override fun applyInverse(v: Double?): Double? {
        return if (v != null) {
            inverseFun(v)
        } else {
            null
        }
    }

    override fun applyInverse(l: List<Double?>): List<Double?> {
        return l.map { applyInverse(it) }
    }

    protected fun safeCastToDoubles(list: List<*>): List<Double?> {
        val checkedDoubles = SeriesUtil.checkedDoubles(list)
        check(checkedDoubles.canBeCast()) { "Not a collections of Double(s)" }
        return checkedDoubles.cast()
    }
}
