/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.*

class SymlogTransform : FunTransform(
    transformFun = ::transformFun,
    inverseFun = ::inverseFun
) {
    override fun hasDomainLimits(): Boolean = false

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v)
    }

    override fun createApplicableDomain(middle: Double?): DoubleSpan {
        return Transforms.IDENTITY.createApplicableDomain(middle)
    }

    override fun toApplicableDomain(range: DoubleSpan): DoubleSpan {
        return Transforms.IDENTITY.toApplicableDomain(range)
    }

    companion object {
        private const val BASE = 10.0
        private const val THRESHOLD = 1.0
        private const val SCALE = 1.0

        internal fun transformFun(v: Double): Double {
            return if (abs(v) < THRESHOLD) {
                v
            } else {
                sign(v) * (THRESHOLD + SCALE * log(sign(v) * v / THRESHOLD, BASE))
            }
        }

        internal fun inverseFun(v: Double): Double {
            return if (abs(v) < THRESHOLD) {
                v
            } else {
                sign(v) * THRESHOLD * BASE.pow((sign(v) * v - THRESHOLD) / SCALE)
            }
        }
    }
}