/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

interface ContinuousTransform : Transform {
    fun hasDomainLimits(): Boolean
    fun isInDomain(v: Double?): Boolean
    fun apply(v: Double?): Double?
    override fun applyInverse(v: Double?): Double?
    fun applyInverse(l: List<Double?>): List<Double?>
}
