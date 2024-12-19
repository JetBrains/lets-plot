/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

interface Transform {
    fun hasDomainLimits(): Boolean
    fun isInDomain(v: Any?): Boolean

    fun apply(l: List<*>): List<Double?>
    fun applyInverse(v: Double?): Any?
    fun applyInverse(l: List<Double?>): List<Any?> {
        return l.map { applyInverse(it) }
    }

    fun unwrap(): Transform {
        return this
    }

    companion object {
        const val MAX_DOUBLE = Double.MAX_VALUE / 1e5
        const val MIN_POSITIVE_DOUBLE = Double.MIN_VALUE * 1e5
    }
}
